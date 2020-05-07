package me.noakviz.cricket.beans.basic;

import me.noakviz.cricket.beans.BeanCopier;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class CopyTest {
    private UserPO createUserPO() {
        return new UserPO("admin", 20, new Date());
    }

    @Test
    public void testGetSetCopy() {
        UserPO po = createUserPO();

        UserVO vo = new UserVO();

        vo.setUsername(po.getUsername());
        vo.setAge(po.getAge());
        vo.setBirthday(po.getBirthday());

        System.out.println(po);
        System.out.println(vo);
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
