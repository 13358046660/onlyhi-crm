package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/29.
 */
public class ProvinceCodeRequest extends BaseRequest {
    @NotBlank(message = "provinceCode不能为空！")
    private String provinceCode;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Override
    public String toString() {
        return "ProvinceCodeRequest{" +
                "provinceCode='" + provinceCode + '\'' +
                "} " + super.toString();
    }
}
