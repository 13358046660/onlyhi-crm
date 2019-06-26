package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public class AuthCodeLoginRequest extends BaseRequest {
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String phone;
    @NotBlank(message = "设备类型不能为空！")
    private String deviceType;
    @NotBlank(message = "用户类型不能为空！")
    private String userType;
    @NotBlank(message = "设备Id不能为空！")
    private String deviceId;
    @NotBlank(message = "验证码不能为空！")
    private String authCode;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "AuthCodeLoginRequest{" +
                "phone='" + phone + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", userType='" + userType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", authCode='" + authCode + '\'' +
                "} " + super.toString();
    }
}
