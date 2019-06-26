package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/12/13.
 */
public class IMUserInfoVo {
    private String imUserName;
    private String iconurl;
    private String userName;

    public String getImUserName() {
        return imUserName;
    }

    public void setImUserName(String imUserName) {
        this.imUserName = imUserName;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "IMUserInfoVo{" +
                "imUserName='" + imUserName + '\'' +
                ", iconurl='" + iconurl + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
