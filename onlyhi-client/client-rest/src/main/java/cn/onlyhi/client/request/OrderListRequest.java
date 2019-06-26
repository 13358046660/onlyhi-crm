package cn.onlyhi.client.request;

import cn.onlyhi.common.request.PageRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/24.
 */
public class OrderListRequest extends PageRequest {
    private Integer payStatus;  //null:全部  0:已关闭;    1:待支付;  2:已支付;
    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    @Override
    public String toString() {
        return "OrderListRequest{" +
                "payStatus=" + payStatus +
                "} " + super.toString();
    }
}
