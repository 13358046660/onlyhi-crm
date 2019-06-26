package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public class RestPsdRequest extends BaseRequest {
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String phone;
    @NotBlank(message = "验证码不能为空！")
    private String authCode;
    @NotBlank(message = "密码不能为空！")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RestPsdRequest{" +
                "phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
