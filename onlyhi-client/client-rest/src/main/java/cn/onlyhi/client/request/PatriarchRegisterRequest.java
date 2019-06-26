package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/13.
 */
public class PatriarchRegisterRequest extends BaseRequest {
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String phone;
    @NotBlank(message = "验证码不能为空！")
    private String authCode;
    /**
     * 家长姓名
     */
    private String patriarchName;
    /**
     * *孩子年级
     */
    private String childrenGrade;
    /**
     * 6-20位密码，支持大小写字母、特殊字符和数字
     * *账号登录密码
     */
    @Length(min=6, max=20)
    private String patriarchPassword;

    public String getPatriarchName() {
        return patriarchName;
    }

    public void setPatriarchName(String patriarchName) {
        this.patriarchName = patriarchName;
    }

    public String getChildrenGrade() {
        return childrenGrade;
    }

    public void setChildrenGrade(String childrenGrade) {
        this.childrenGrade = childrenGrade;
    }

    public String getPatriarchPassword() {
        return patriarchPassword;
    }

    public void setPatriarchPassword(String patriarchPassword) {
        this.patriarchPassword = patriarchPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Override
    public String toString() {
        return "PatriarchRegisterRequest{" +
                "phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                "} " + super.toString();
    }
}
