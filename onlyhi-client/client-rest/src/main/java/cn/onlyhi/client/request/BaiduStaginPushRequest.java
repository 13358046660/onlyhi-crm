package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/27.
 */
public class BaiduStaginPushRequest extends BaseRequest {
    private String orderid;
    private Integer status;
    private Integer period;
    private String sign;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "BaiduStaginPushRequest{" +
                "orderid='" + orderid + '\'' +
                ", status=" + status +
                ", period=" + period +
                ", sign='" + sign + '\'' +
                "} " + super.toString();
    }
}
