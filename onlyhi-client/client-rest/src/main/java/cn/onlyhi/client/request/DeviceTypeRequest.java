package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/24.
 */
public class DeviceTypeRequest extends BaseRequest {
    @NotBlank(message = "deviceType不能为空！")
    private String deviceType;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "DeviceTypeRequest{" +
                "deviceType='" + deviceType + '\'' +
                "} " + super.toString();
    }
}
