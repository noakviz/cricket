package me.noakviz.cricket.beans;

import java.util.Objects;

public class CopierKey {
    private Class<?> fromClass;
    private Class<?> toClass;

    public CopierKey(Class<?> fromClass, Class<?> toClass) {
        this.fromClass = fromClass;
        this.toClass = toClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CopierKey key = (CopierKey) o;
        return Objects.equals(fromClass, key.fromClass) &&
                Objects.equals(toClass, key.toClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromClass, toClass);
    }
}
