package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public class EmpLoginRequest extends BaseRequest {
    @NotBlank(message = "登录名不能为空！")
    private String loginName;
    @NotBlank(message = "密码不能为空！")
    private String password;
    @NotBlank(message = "设备类型不能为空！")
    private String deviceType;
    @NotBlank(message = "用户类型不能为空！")
    private String userType;
    @NotBlank(message = "时间戳不能为空！")
    private String timestamp;
    @NotBlank(message = "设备Id不能为空！")
    private String deviceId;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "EmpLoginRequest{" +
                "loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", userType='" + userType + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", deviceId='" + deviceId + '\'' +
                "} " + super.toString();
    }
}
