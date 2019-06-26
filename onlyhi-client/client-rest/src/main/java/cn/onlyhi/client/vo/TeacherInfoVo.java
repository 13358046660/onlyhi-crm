package cn.onlyhi.client.vo;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/1/11.
 */
public class TeacherInfoVo {
    private String grade;   //等级
    private String name;    //姓名
    private double hour;    //本月已上课时，单位h
    private String surpassRatio;    //已上课时超过其他教师百分比
    private String photo;   //头像

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    @Override
    public String toString() {
        return "TeacherInfoVo{" +
                "grade='" + grade + '\'' +
                ", name='" + name + '\'' +
                ", hour=" + hour +
                ", surpassRatio='" + surpassRatio + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
