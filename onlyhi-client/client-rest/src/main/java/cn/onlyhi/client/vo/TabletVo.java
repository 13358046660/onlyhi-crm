package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/15.
 */
public class TabletVo {
    private String teacherName;
    private String phone;
    private String postAddress;
    private int isOrder;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public int getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(int isOrder) {
        this.isOrder = isOrder;
    }

    @Override
    public String toString() {
        return "TabletVo{" +
                "teacherName='" + teacherName + '\'' +
                ", phone='" + phone + '\'' +
                ", postAddress='" + postAddress + '\'' +
                ", isOrder=" + isOrder +
                '}';
    }
}
