package me.noakviz.cricket.beans.inheritance;

import me.noakviz.cricket.beans.BeanCopier;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class CopyTest {
    private UserPO createUserPO() {
        UserPO po = new UserPO();
        po.setUsername("admin");
        po.setAge(20);
        po.setBirthday(new Date());
        po.setAccount("bbq");
        return po;
    }

    @Test
    public void testCopierCopy() throws IllegalAccessException, InvocationTargetException {
        UserPO po = createUserPO();

        UserVO vo = new UserVO();

        BeanCopier.copy(po, vo);

        System.out.println(po);
        System.out.println(vo);
    }
}
