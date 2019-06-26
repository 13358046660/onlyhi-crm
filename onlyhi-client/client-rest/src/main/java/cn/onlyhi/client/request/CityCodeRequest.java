package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/29.
 */
public class CityCodeRequest extends BaseRequest {
    @NotBlank(message = "cityCode不能为空！")
    private String cityCode;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String toString() {
        return "CityCodeRequest{" +
                "cityCode='" + cityCode + '\'' +
                "} " + super.toString();
    }
}
