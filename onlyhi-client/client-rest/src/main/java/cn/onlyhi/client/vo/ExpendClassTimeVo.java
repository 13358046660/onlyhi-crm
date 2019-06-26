package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/25.
 */
public class ExpendClassTimeVo {
    private double expendOrdinaryTime;
    private double expendQbTime;
    private double expendQualityTime;

    public double getExpendOrdinaryTime() {
        return expendOrdinaryTime;
    }

    public void setExpendOrdinaryTime(double expendOrdinaryTime) {
        this.expendOrdinaryTime = expendOrdinaryTime;
    }

    public double getExpendQbTime() {
        return expendQbTime;
    }

    public void setExpendQbTime(double expendQbTime) {
        this.expendQbTime = expendQbTime;
    }

    public double getExpendQualityTime() {
        return expendQualityTime;
    }

    public void setExpendQualityTime(double expendQualityTime) {
        this.expendQualityTime = expendQualityTime;
    }

    @Override
    public String toString() {
        return "ExpendClassTimeVo{" +
                "expendOrdinaryTime=" + expendOrdinaryTime +
                ", expendQbTime=" + expendQbTime +
                ", expendQualityTime=" + expendQualityTime +
                '}';
    }
}
