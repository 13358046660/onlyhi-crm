package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/28.
 */
public class PingppOrderPayStatusRequest extends BaseRequest {
    private String chargeId;    //ping++订单charge对象的id

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    @Override
    public String toString() {
        return "PingppOrderPayStatusRequest{" +
                "chargeId='" + chargeId + '\'' +
                "} " + super.toString();
    }
}
