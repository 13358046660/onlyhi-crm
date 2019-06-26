package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/25.
 */
public class PayMoneyRequest extends BaseRequest {
    @NotBlank(message = "课时包uuid不能为空！")
    private String coursePriceUuid; //课时包uuid
    private String code;    //优惠码

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

    @Override
    public String toString() {
        return "PayMoneyRequest{" +
                "coursePriceUuid='" + coursePriceUuid + '\'' +
                ", code='" + code + '\'' +
                "} " + super.toString();
    }
}
