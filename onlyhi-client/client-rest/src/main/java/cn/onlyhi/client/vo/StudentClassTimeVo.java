package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/13.
 */
public class StudentClassTimeVo {
    private double ordinaryTotalTime;   //普通总课时
    private double qbTotalTime;         //清北总课时
    private double qualityTotalTime;    //精品总课时
    private double ordinarySurplusTime; //普通剩余课时
    private double qbSurplusTime;       //清北剩余课时
    private double qualitySurplusTime;  //精品剩余课时

    public double getOrdinaryTotalTime() {
        return ordinaryTotalTime;
    }

    public void setOrdinaryTotalTime(double ordinaryTotalTime) {
        this.ordinaryTotalTime = ordinaryTotalTime;
    }

    public double getQbTotalTime() {
        return qbTotalTime;
    }

    public void setQbTotalTime(double qbTotalTime) {
        this.qbTotalTime = qbTotalTime;
    }

    public double getQualityTotalTime() {
        return qualityTotalTime;
    }

    public void setQualityTotalTime(double qualityTotalTime) {
        this.qualityTotalTime = qualityTotalTime;
    }

    public double getOrdinarySurplusTime() {
        return ordinarySurplusTime;
    }

    public void setOrdinarySurplusTime(double ordinarySurplusTime) {
        this.ordinarySurplusTime = ordinarySurplusTime;
    }

    public double getQbSurplusTime() {
        return qbSurplusTime;
    }

    public void setQbSurplusTime(double qbSurplusTime) {
        this.qbSurplusTime = qbSurplusTime;
    }

    public double getQualitySurplusTime() {
        return qualitySurplusTime;
    }

    public void setQualitySurplusTime(double qualitySurplusTime) {
        this.qualitySurplusTime = qualitySurplusTime;
    }

    @Override
    public String toString() {
        return "StudentClassTimeVo{" +
                "ordinaryTotalTime=" + ordinaryTotalTime +
                ", qbTotalTime=" + qbTotalTime +
                ", qualityTotalTime=" + qualityTotalTime +
                ", ordinarySurplusTime=" + ordinarySurplusTime +
                ", qbSurplusTime=" + qbSurplusTime +
                ", qualitySurplusTime=" + qualitySurplusTime +
                '}';
    }
}
