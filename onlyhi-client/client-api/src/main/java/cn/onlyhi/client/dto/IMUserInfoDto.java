package cn.onlyhi.client.dto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/5.
 */
public class IMUserInfoDto extends BaseDto {
    private String imUserName;
    private String iconurl;
    private String userName;
    private String phone;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "IMUserInfoDto{" +
                "imUserName='" + imUserName + '\'' +
                ", iconurl='" + iconurl + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                "} " + super.toString();
    }
}
