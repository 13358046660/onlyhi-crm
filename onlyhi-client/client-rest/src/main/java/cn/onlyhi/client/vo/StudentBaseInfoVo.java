package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/13.
 */
public class StudentBaseInfoVo {
    private String photo;   //头像
    private String name;    //姓名
    private double hour;    //本月已上课时，单位h
    private String surpassRatio;    //本月已上课时超过其他学生百分比
    private int noJoinClassCount;  //本月没有参加课程的节数
    private String attendanceRate;  //本月出勤率：参加课程节数/总节数 0%
    private boolean isSign; //是否已签到 true:已签到    false:未签到

    public boolean getIsSign() {
        return isSign;
    }

    public void setIsSign(boolean isSign) {
        this.isSign = isSign;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHour() {
        return hour;
    }

    public void setHour(double hour) {
        this.hour = hour;
    }

    public String getSurpassRatio() {
        return surpassRatio;
    }

    public void setSurpassRatio(String surpassRatio) {
        this.surpassRatio = surpassRatio;
    }

    public int getNoJoinClassCount() {
        return noJoinClassCount;
    }

    public void setNoJoinClassCount(int noJoinClassCount) {
        this.noJoinClassCount = noJoinClassCount;
    }

    public String getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(String attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    @Override
    public String toString() {
        return "StudentBaseInfoVo{" +
                "photo='" + photo + '\'' +
                ", name='" + name + '\'' +
                ", hour=" + hour +
                ", surpassRatio='" + surpassRatio + '\'' +
                ", noJoinClassCount=" + noJoinClassCount +
                ", attendanceRate='" + attendanceRate + '\'' +
                ", isSign=" + isSign +
                '}';
    }
}
