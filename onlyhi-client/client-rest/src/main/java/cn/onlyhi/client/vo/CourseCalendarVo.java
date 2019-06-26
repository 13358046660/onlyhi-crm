package cn.onlyhi.client.vo;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/1/11.
 */
public class CourseCalendarVo {
    private int dateDay;    //日期
    private int dateStatus;   //0:不属于这个月的日期，1:没有课，2:有课
    private boolean isToday;    //是否是今天，true：是，false：否

    public int getDateDay() {
        return dateDay;
    }

    public void setDateDay(int dateDay) {
        this.dateDay = dateDay;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }

    public boolean getIsToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }

    @Override
    public String toString() {
        return "CourseCalendarVo{" +
                "dateDay=" + dateDay +
                ", dateStatus=" + dateStatus +
                ", isToday=" + isToday +
                '}';
    }
}
