package me.noakviz.cricket.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectionCopier implements Copier {
    List<PropertyMapping> mappings;

    public ReflectionCopier(List<PropertyMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void copy(Object from, Object to) {
        for (PropertyMapping mapping : mappings) {
            Method writeMethod = mapping.getWriteMethod();
            Method readMethod = mapping.getReadMethod();
            try {
                Object value = readMethod.invoke(from);
                writeMethod.invoke(to, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("无法调用 [" + readMethod.getName() + "] 或者 [" + writeMethod.getName() + "] 的 invoke 方法进行属性复制", e);
            }
        }
    }
}
