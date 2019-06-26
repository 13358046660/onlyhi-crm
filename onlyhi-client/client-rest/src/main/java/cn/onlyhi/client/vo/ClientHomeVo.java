package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/25.
 */
public class ClientHomeVo {
    private String iconurl;     //头像
    private String userName;    //用户姓名
    private double classTime;   //消耗的课时 0.0
    private String surpassRatio;       //消耗课时超过其他人比率; 0%
    private int noJoinClassCount;  //没有参加课程的节数
    private String attendanceRate;  //出勤率：参加课程节数/总节数 0%

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getClassTime() {
        return classTime;
    }

    public void setClassTime(double classTime) {
        this.classTime = classTime;
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
        return "ClientHomeVo{" +
                "iconurl='" + iconurl + '\'' +
                ", userName='" + userName + '\'' +
                ", classTime=" + classTime +
                ", surpassRatio='" + surpassRatio + '\'' +
                ", noJoinClassCount=" + noJoinClassCount +
                ", attendanceRate='" + attendanceRate + '\'' +
                '}';
    }
}
