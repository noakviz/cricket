package me.noakviz.cricket.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

public class IntrospectionResult {
    private final BeanInfo beanInfo;

    private final Map<String, PropertyDescriptor> propertyDescriptorCache;

    //propertyName -> aliasList
    private final Map<String, List<String>> aliasCache = new HashMap<>();

    //alias -> propertyNameList
    private final Map<String, List<String>> reversedAliasCache = new HashMap<>();

    public IntrospectionResult(Class<?> clazz) {
        try {
            this.beanInfo = Introspector.getBeanInfo(clazz);
            this.propertyDescriptorCache = new LinkedHashMap<>();

            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                this.propertyDescriptorCache.put(pd.getName(), pd);
            }

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                CopyProperty copyProperty = field.getAnnotation(CopyProperty.class);
                if (copyProperty == null) {
                    continue;
                }
                String[] aliasList = copyProperty.alias();
                if (aliasList.length == 0) {
                    continue;
                }
                aliasCache.put(field.getName(), new ArrayList<>(Arrays.asList(aliasList)));
                for (String alias : aliasList) {
                    List<String> propertyNames = reversedAliasCache.computeIfAbsent(alias, k -> new ArrayList<>());
                    propertyNames.add(field.getName());
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException("获取类 [" + clazz.getName() + "] 的 BeanInfo 失败", e);
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return beanInfo.getPropertyDescriptors();
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        return propertyDescriptorCache.get(propertyName);
    }

    public List<String> getAliasList(String propertyName) {
        return aliasCache.get(propertyName);
    }

    public List<String> getReversedAliasList(String propertyName) {
        return reversedAliasCache.get(propertyName);
    }
}
