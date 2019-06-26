package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/4.
 */
public class AppInfoRequest extends BaseRequest {
    @NotBlank(message = "设备类型不能为空！")
    private String deviceType;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "AppInfoRequest{" +
                "deviceType='" + deviceType + '\'' +
                "} " + super.toString();
    }
}
