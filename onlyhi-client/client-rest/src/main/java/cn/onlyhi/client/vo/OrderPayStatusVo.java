package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/28.
 */
public class OrderPayStatusVo {
    private int payStatus;

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    @Override
    public String toString() {
        return "OrderPayStatusVo{" +
                "payStatus=" + payStatus +
                '}';
    }
}
