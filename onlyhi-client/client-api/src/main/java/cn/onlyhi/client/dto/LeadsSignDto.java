package cn.onlyhi.client.dto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/17.
 */
public class LeadsSignDto extends BaseDto {
    private int currentIntegral;    //当前签到积分
    private int contDays;   //连续签到天数
    private int tomorrowIntegral;   //明天签到的积分

    public int getCurrentIntegral() {
        return currentIntegral;
    }

    public void setCurrentIntegral(int currentIntegral) {
        this.currentIntegral = currentIntegral;
    }

    public int getContDays() {
        return contDays;
    }

    public void setContDays(int contDays) {
        this.contDays = contDays;
    }

    public int getTomorrowIntegral() {
        return tomorrowIntegral;
    }

    public void setTomorrowIntegral(int tomorrowIntegral) {
        this.tomorrowIntegral = tomorrowIntegral;
    }
}
