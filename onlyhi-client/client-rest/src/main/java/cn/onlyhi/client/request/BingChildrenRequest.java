package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/5.
 */
public class BingChildrenRequest extends BaseRequest {
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "学生手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "学生手机号格式错误！")
    private String phone;   //学生phone
    @NotBlank(message = "孩子登录密码不能为空!")
    private String password;
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "家长手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "家长手机号格式错误！")
    private String patriarchPhone;

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

    public String getPatriarchPhone() {
        return patriarchPhone;
    }

    public void setPatriarchPhone(String patriarchPhone) {
        this.patriarchPhone = patriarchPhone;
    }

    @Override
    public String toString() {
        return "BingChildrenRequest{" +
                "phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", patriarchPhone='" + patriarchPhone + '\'' +
                "} " + super.toString();
    }
}
