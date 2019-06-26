package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/5/23.
 */
public class BingRequest extends BaseRequest {
    @NotBlank(message = "deviceToken不能为空！")
    private String deviceToken;
    @NotBlank(message = "tag不能为空！")
    private String tag;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "BingRequest{" +
                "deviceToken='" + deviceToken + '\'' +
                ", tag='" + tag + '\'' +
                "} " + super.toString();
    }
}
