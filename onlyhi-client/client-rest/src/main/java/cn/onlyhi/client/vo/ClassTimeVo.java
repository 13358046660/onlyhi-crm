package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/14.
 */
public class ClassTimeVo {

    private String classPackageName;    //课时包名称

    private String totalTime;   //总课时

    private String surplusTime;   //剩余课时

    public String getClassPackageName() {
        return classPackageName;
    }

    public void setClassPackageName(String classPackageName) {
        this.classPackageName = classPackageName;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getSurplusTime() {
        return surplusTime;
    }

    public void setSurplusTime(String surplusTime) {
        this.surplusTime = surplusTime;
    }

    @Override
    public String toString() {
        return "ClassTimeVo{" +
                "classPackageName='" + classPackageName + '\'' +
                ", totalTime='" + totalTime + '\'' +
                ", surplusTime='" + surplusTime + '\'' +
                '}';
    }
}
