package me.noakviz.cricket.beans.basic;

import java.io.Serializable;
import java.util.Date;

public class UserPO implements Serializable {
    private static final long serialVersionUID = 2318730978348627305L;

    private String username;

    private int age;

    private Date birthday;

    public UserPO() {

    }

    public UserPO(String username, int age, Date birthday) {
        this.username = username;
        this.age = age;
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "UserDO{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                '}';
    }
}
