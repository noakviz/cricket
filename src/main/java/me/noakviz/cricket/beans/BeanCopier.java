package me.noakviz.cricket.beans;

public class BeanCopier {
    private static CopierCache cache = new CopierCache();

    public static <T> T copy(Object from, Class<T> toClass, String... ignoreProperties) {
        try {
            T target = toClass.newInstance();
            copy(from, target, ignoreProperties);
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("创建 [" + toClass.getName() + "] 的实例失败");
        }
    }

    public static void copy(Object from, Object to, String... ignoreProperties) {
        Copier copier = cache.getCopier(from.getClass(), to.getClass(), ignoreProperties);
        copier.copy(from, to);
    }
}
