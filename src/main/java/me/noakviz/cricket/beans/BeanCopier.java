package me.noakviz.cricket.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class BeanCopier {
    private static final Map<Class<?>, IntrospectionResult> introspectionResultCache = new HashMap<>();

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

    private static final Map<CopierKey, Copier> copierCache = new HashMap<>();

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        primitiveWrapperTypeMap.put(Void.class, void.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static void copy(Object from, Object to) {
        Copier copier = getCopier(from.getClass(), to.getClass());
        copier.copy(from, to);
    }

    private static IntrospectionResult getIntrospectionResult(Class<?> clazz) {
        IntrospectionResult introspectionResult = introspectionResultCache.get(clazz);
        if (introspectionResult != null) {
            return introspectionResult;
        }
        introspectionResult = new IntrospectionResult(clazz);
        synchronized (introspectionResultCache) {
            IntrospectionResult existing = introspectionResultCache.putIfAbsent(clazz, introspectionResult);
            return existing != null ? existing : introspectionResult;
        }
    }

    private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        return getIntrospectionResult(clazz).getPropertyDescriptors();
    }

    private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        return getIntrospectionResult(clazz).getPropertyDescriptor(propertyName);
    }

    private static List<PropertyDescriptor> getCandidatePropertyDescriptors(Class<?> clazz, List<String> aliasList) {
        List<PropertyDescriptor> candidates = new ArrayList<>();
        for (String alias : aliasList) {
            PropertyDescriptor candidate = getIntrospectionResult(clazz).getPropertyDescriptor(alias);
            if (candidate != null) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private static List<String> getAliasList(Class<?> clazz, String propertyName) {
        List<String> aliasList = getIntrospectionResult(clazz).getAliasList(propertyName);
        if (aliasList == null) {
            return Collections.emptyList();
        }
        return aliasList;
    }

    private static List<String> getReversedAliasList(Class<?> clazz, String propertyName) {
        List<String> reversedAliasList = getIntrospectionResult(clazz).getReversedAliasList(propertyName);
        if (reversedAliasList == null) {
            return Collections.emptyList();
        }
        return reversedAliasList;
    }

    private static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            return (lhsType == resolvedPrimitive);
        } else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
        }
    }

    private static Copier getCopier(Class<?> fromClass, Class<?> toClass) {
        CopierKey key = new CopierKey(fromClass, toClass);

        Copier copier = copierCache.get(key);
        if (copier != null) {
            return copier;
        }

        List<PropertyMapping> propertyMappings = buildPropertyMapping(fromClass, toClass);
        copier = new ReflectionCopier(propertyMappings);
        Copier existing = copierCache.putIfAbsent(key, copier);

        return existing != null ? existing : copier;
    }

    private static List<PropertyMapping> buildPropertyMapping(Class<?> fromClass, Class<?> toClass) {
        List<PropertyMapping> propertyMappings = new ArrayList<>();

        PropertyDescriptor[] targetPropertyDescriptors = getPropertyDescriptors(toClass);

        for (PropertyDescriptor targetPropertyDescriptor : targetPropertyDescriptors) {
            String targetPropertyName = targetPropertyDescriptor.getName();
            if ("class".equals(targetPropertyName)) {
                continue;
            }
            Method writeMethod = targetPropertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                continue;
            }
            PropertyDescriptor sourcePropertyDescriptor = getPropertyDescriptor(fromClass, targetPropertyName);
            if (sourcePropertyDescriptor == null) {
                List<PropertyDescriptor> candidates = getCandidatePropertyDescriptors(fromClass, getAliasList(toClass, targetPropertyName));
                if (candidates.size() == 1) {
                    sourcePropertyDescriptor = candidates.get(0);
                } else {
                    candidates = getCandidatePropertyDescriptors(fromClass, getReversedAliasList(fromClass, targetPropertyName));
                    if (candidates.size() == 1) {
                        sourcePropertyDescriptor = candidates.get(0);
                    }
                }
            }
            if (sourcePropertyDescriptor == null) {
                continue;
            }
            Method readMethod = sourcePropertyDescriptor.getReadMethod();
            if (readMethod == null) {
                continue;
            }
            if (isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                PropertyMapping propertyMapping = new PropertyMapping();
                propertyMapping.setPropertyName(targetPropertyName);
                propertyMapping.setWriteMethod(writeMethod);
                propertyMapping.setReadMethod(readMethod);
                propertyMappings.add(propertyMapping);
            }
        }

        return propertyMappings;
    }
}
