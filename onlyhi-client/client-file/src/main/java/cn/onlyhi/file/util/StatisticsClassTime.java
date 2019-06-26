package cn.onlyhi.file.util;

import java.util.Map;

/**
 * 统计有效时间段
 *
 * @Author wqz
 * <p>
 * Created by wqz on 2018/09/29.
 */
public class StatisticsClassTime {
    private String startTime;  //学生和老师共同在线开始时间   单位分钟
    private String endTime;  //老师在线结束时间  单位分钟
    private Map<Long, Long> totalTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Map<Long, Long> getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Map<Long, Long> totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "StatisticsClassTime{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalTime=" + totalTime +
                '}';
    }
}

