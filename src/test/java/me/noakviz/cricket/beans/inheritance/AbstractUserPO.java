package me.noakviz.cricket.beans.inheritance;

public class AbstractUserPO {
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "AbstractUserPO{" +
                "account='" + account + '\'' +
                '}';
    }
}
