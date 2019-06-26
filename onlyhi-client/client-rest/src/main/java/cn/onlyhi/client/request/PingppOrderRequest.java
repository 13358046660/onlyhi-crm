package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/26.
 */
public class PingppOrderRequest extends BaseRequest {
    @NotBlank(message = "orderUuid不能为空！")
    private String orderUuid;   //即cp_pay_request的uuid
    @NotBlank(message = "channel不能为空！")
    private String channel;

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "PingppOrderRequest{" +
                "orderUuid='" + orderUuid + '\'' +
                ", channel='" + channel + '\'' +
                "} " + super.toString();
    }
}
