package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/3.
 */
public class UserRegisterVo {
    private boolean registerFlag;
    private String userName;

    public boolean isRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(boolean registerFlag) {
        this.registerFlag = registerFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserRegisterVo{" +
                "registerFlag=" + registerFlag +
                ", userName='" + userName + '\'' +
                '}';
    }
}
