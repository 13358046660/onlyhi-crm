package cn.onlyhi.file.util;

/**
 * 课时统计结果(课程安排时间内的统计)
 *
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/2/5.
 */
public class StatisticsClassTimeResult {
    private Integer comTotalTime;  //学生和老师共同在线时长   单位m
    private Integer teacherTotalTime;  //老师在线总时长   单位m
    private Integer studentTotalTime;  //学生在线总时长   单位m
    /**
     * 开课是否成功   0：失败   1：成功    2:待反馈（调试中）  3:异常    4：已处理
     */
    private Integer isSuccess;

    public Integer getComTotalTime() {
        return comTotalTime;
    }

    public void setComTotalTime(Integer comTotalTime) {
        this.comTotalTime = comTotalTime;
    }

    public Integer getTeacherTotalTime() {
        return teacherTotalTime;
    }

    public void setTeacherTotalTime(Integer teacherTotalTime) {
        this.teacherTotalTime = teacherTotalTime;
    }

    public Integer getStudentTotalTime() {
        return studentTotalTime;
    }

    public void setStudentTotalTime(Integer studentTotalTime) {
        this.studentTotalTime = studentTotalTime;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return "StatisticsClassTimeResult{" +
                "comTotalTime=" + comTotalTime +
                ", teacherTotalTime=" + teacherTotalTime +
                ", studentTotalTime=" + studentTotalTime +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
