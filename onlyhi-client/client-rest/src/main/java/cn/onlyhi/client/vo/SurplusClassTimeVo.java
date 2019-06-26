package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/25.
 */
public class SurplusClassTimeVo {
    private double surplusOrdinaryTime;
    private double surplusQbTime;
    private double surplusQualityTime;

    public double getSurplusOrdinaryTime() {
        return surplusOrdinaryTime;
    }

    public void setSurplusOrdinaryTime(double surplusOrdinaryTime) {
        this.surplusOrdinaryTime = surplusOrdinaryTime;
    }

    public double getSurplusQbTime() {
        return surplusQbTime;
    }

    public void setSurplusQbTime(double surplusQbTime) {
        this.surplusQbTime = surplusQbTime;
    }

    public double getSurplusQualityTime() {
        return surplusQualityTime;
    }

    public void setSurplusQualityTime(double surplusQualityTime) {
        this.surplusQualityTime = surplusQualityTime;
    }

    @Override
    public String toString() {
        return "SurplusClassTimeVo{" +
                "surplusOrdinaryTime=" + surplusOrdinaryTime +
                ", surplusQbTime=" + surplusQbTime +
                ", surplusQualityTime=" + surplusQualityTime +
                '}';
    }
}
