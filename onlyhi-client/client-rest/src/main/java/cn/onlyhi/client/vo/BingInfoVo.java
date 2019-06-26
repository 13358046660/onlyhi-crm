package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/7.
 */
public class BingInfoVo {
    private Integer bingStatus; //绑定状态  1:未绑定   2:已绑定   3:被解绑
    private String phone;   //绑定的家长手机号
    private String childrenName;    //孩子姓名

    public Integer getBingStatus() {
        return bingStatus;
    }

    public void setBingStatus(Integer bingStatus) {
        this.bingStatus = bingStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getChildrenName() {
        return childrenName;
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
    }

    @Override
    public String toString() {
        return "BingInfoVo{" +
                "bingStatus=" + bingStatus +
                ", phone='" + phone + '\'' +
                ", childrenName='" + childrenName + '\'' +
                '}';
    }
}
