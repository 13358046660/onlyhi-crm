package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/13.
 */
public class CheckRegisterRequest extends BaseRequest {
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String phone;
    @NotBlank(message = "用户类型不能为空！")
    private String userType;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "CheckRegisterRequest{" +
                "phone='" + phone + '\'' +
                ", userType='" + userType + '\'' +
                "} " + super.toString();
    }
}
