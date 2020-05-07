package me.noakviz.cricket.beans.primitive;

import me.noakviz.cricket.beans.BeanCopier;
import org.junit.Test;

public class Test01 {
    private OrigBean getOrigBean() {
        OrigBean origBean = new OrigBean();
        origBean.setUsable(true);
        origBean.setAge(20);
        origBean.setWidth(15);
        origBean.setHeight(10);
        origBean.setName("管理员");
        return origBean;
    }

    @Test
    public void testPrimitiveType() {
        OrigBean orig = getOrigBean();
        DestBean dest = new DestBean();

        BeanCopier.copy(orig, dest);

        System.out.println(orig);
        System.out.println(dest);

        OrigBean orig1 = getOrigBean();
        DestBean dest1 = new DestBean();

        BeanCopier.copy(orig1, dest1);

        System.out.println(orig1);
        System.out.println(dest1);
    }
}
