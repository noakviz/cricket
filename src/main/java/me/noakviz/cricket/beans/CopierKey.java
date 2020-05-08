package me.noakviz.cricket.beans;

import java.util.Arrays;
import java.util.Objects;

public class CopierKey {
    private Class<?> fromClass;
    private Class<?> toClass;
    private String[] ignoreProperties;

    public CopierKey(Class<?> fromClass, Class<?> toClass, String[] ignoreProperties) {
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.ignoreProperties = ignoreProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CopierKey copierKey = (CopierKey) o;
        return Objects.equals(fromClass, copierKey.fromClass) &&
                Objects.equals(toClass, copierKey.toClass) &&
                Arrays.equals(ignoreProperties, copierKey.ignoreProperties);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fromClass, toClass);
        result = 31 * result + Arrays.hashCode(ignoreProperties);
        return result;
    }
}
