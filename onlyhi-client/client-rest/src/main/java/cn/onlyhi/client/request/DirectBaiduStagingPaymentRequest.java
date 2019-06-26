package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/27.
 */
public class DirectBaiduStagingPaymentRequest extends BaseRequest {
    @NotBlank(message = "课时包uuid不能为空！")
    private String coursePriceUuid; //课时包uuid
    private String code;    //优惠码
    @NotBlank(message = "姓名不能为空！")
    private String patriarchName;   //家长姓名即贷款人姓名
    @NotBlank(message = "手机号不能为空！")
//    @Pattern(regexp = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$", message = "手机号格式错误！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String patriarchPhone;  //家长手机号

    public String getCoursePriceUuid() {
        return coursePriceUuid;
    }

    public void setCoursePriceUuid(String coursePriceUuid) {
        this.coursePriceUuid = coursePriceUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPatriarchName() {
        return patriarchName;
    }

    public void setPatriarchName(String patriarchName) {
        this.patriarchName = patriarchName;
    }

    public String getPatriarchPhone() {
        return patriarchPhone;
    }

    public void setPatriarchPhone(String patriarchPhone) {
        this.patriarchPhone = patriarchPhone;
    }

    @Override
    public String toString() {
        return "DirectBaiduStagingPaymentRequest{" +
                "coursePriceUuid='" + coursePriceUuid + '\'' +
                ", code='" + code + '\'' +
                ", patriarchName='" + patriarchName + '\'' +
                ", patriarchPhone='" + patriarchPhone + '\'' +
                "} " + super.toString();
    }
}
