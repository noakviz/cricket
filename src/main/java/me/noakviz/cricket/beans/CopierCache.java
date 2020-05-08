package me.noakviz.cricket.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

public class CopierCache {
    private final Map<Class<?>, IntrospectionResult> introspectionResultMap = new HashMap<>();

    private final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    private final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

    private final Map<CopierKey, Copier> copierMap = new HashMap<>();

    public CopierCache() {
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

    private IntrospectionResult getIntrospectionResult(Class<?> clazz) {
        IntrospectionResult introspectionResult = introspectionResultMap.get(clazz);
        if (introspectionResult != null) {
            return introspectionResult;
        }
        introspectionResult = new IntrospectionResult(clazz);
        synchronized (introspectionResultMap) {
            IntrospectionResult existing = introspectionResultMap.putIfAbsent(clazz, introspectionResult);
            return existing != null ? existing : introspectionResult;
        }
    }

    private PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        return getIntrospectionResult(clazz).getPropertyDescriptors();
    }

    private PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        return getIntrospectionResult(clazz).getPropertyDescriptor(propertyName);
    }

    private List<PropertyDescriptor> getCandidatePropertyDescriptors(Class<?> clazz, List<String> aliasList) {
        List<PropertyDescriptor> candidates = new ArrayList<>();
        for (String alias : aliasList) {
            PropertyDescriptor candidate = getIntrospectionResult(clazz).getPropertyDescriptor(alias);
            if (candidate != null) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private List<String> getAliasList(Class<?> clazz, String propertyName) {
        List<String> aliasList = getIntrospectionResult(clazz).getAliasList(propertyName);
        if (aliasList == null) {
            return Collections.emptyList();
        }
        return aliasList;
    }

    private List<String> getReversedAliasList(Class<?> clazz, String propertyName) {
        List<String> reversedAliasList = getIntrospectionResult(clazz).getReversedAliasList(propertyName);
        if (reversedAliasList == null) {
            return Collections.emptyList();
        }
        return reversedAliasList;
    }

    private boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
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

    public Copier getCopier(Class<?> fromClass, Class<?> toClass, String[] ignoreProperties) {
        CopierKey key = new CopierKey(fromClass, toClass, ignoreProperties);

        Copier copier = copierMap.get(key);
        if (copier != null) {
            return copier;
        }

        List<PropertyMapping> propertyMappings = buildPropertyMapping(fromClass, toClass, ignoreProperties);
        copier = new ReflectionCopier(propertyMappings);
        Copier existing = copierMap.putIfAbsent(key, copier);

        return existing != null ? existing : copier;
    }

    private List<PropertyMapping> buildPropertyMapping(Class<?> fromClass, Class<?> toClass, String[] ignoreProperties) {
        List<PropertyMapping> propertyMappings = new ArrayList<>();

        PropertyDescriptor[] targetPropertyDescriptors = getPropertyDescriptors(toClass);

        for (PropertyDescriptor targetPropertyDescriptor : targetPropertyDescriptors) {
            String targetPropertyName = targetPropertyDescriptor.getName();
            if ("class".equals(targetPropertyName)) {
                continue;
            }
            if (isIgnoreProperty(ignoreProperties, targetPropertyName)) {
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

    private boolean isIgnoreProperty(String[] properties, String property) {
        for (String p : properties) {
            if (property.equals(p)) {
                return true;
            }
        }
        return false;
    }
}
