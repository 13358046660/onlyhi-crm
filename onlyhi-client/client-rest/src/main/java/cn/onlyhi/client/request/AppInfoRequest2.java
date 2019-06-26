package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/4.
 */
public class AppInfoRequest2 extends BaseRequest {
    @NotBlank(message = "设备类型不能为空！")
    private String deviceType;
    @NotBlank(message = "用户类型不能为空！")
    private String userType;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "AppInfoRequest2{" +
                "deviceType='" + deviceType + '\'' +
                ", userType='" + userType + '\'' +
                "} " + super.toString();
    }
}
