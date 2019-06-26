package cn.onlyhi.client.vo;

import java.math.BigDecimal;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/6/22.
 */
public class CourseListVo {
    private String courseUuid;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String subject;
    private Integer channelTeacherId;
    private Integer channelPatriarchId;
    private String teacherName;
    private String name;    //学生姓名
    private BigDecimal classTime;
    private String classAppraiseUuid;   //学生对老师上课评价uuid，若为空则未评价
    private String classTeacherAppraiseUuid;//老师对学生上课的反馈
    private String grade;
    private Integer courseType; //课程类型：0：测评课1：正式课 2.调试课

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getClassTeacherAppraiseUuid() {
		return classTeacherAppraiseUuid;
	}

	public void setClassTeacherAppraiseUuid(String classTeacherAppraiseUuid) {
		this.classTeacherAppraiseUuid = classTeacherAppraiseUuid;
	}

	public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getChannelTeacherId() {
        return channelTeacherId;
    }

    public void setChannelTeacherId(Integer channelTeacherId) {
        this.channelTeacherId = channelTeacherId;
    }

    public Integer getChannelPatriarchId() {
        return channelPatriarchId;
    }

    public void setChannelPatriarchId(Integer channelPatriarchId) {
        this.channelPatriarchId = channelPatriarchId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getClassTime() {
        return classTime;
    }

    public void setClassTime(BigDecimal classTime) {
        this.classTime = classTime;
    }

    public String getClassAppraiseUuid() {
        return classAppraiseUuid;
    }

    public void setClassAppraiseUuid(String classAppraiseUuid) {
        this.classAppraiseUuid = classAppraiseUuid;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    @Override
    public String toString() {
        return "CourseListVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", channelTeacherId=" + channelTeacherId +
                ", channelPatriarchId=" + channelPatriarchId +
                ", teacherName='" + teacherName + '\'' +
                ", name='" + name + '\'' +
                ", classTime=" + classTime +
                ", classAppraiseUuid='" + classAppraiseUuid + '\'' +
                ", classTeacherAppraiseUuid='" + classTeacherAppraiseUuid + '\'' +
                ", grade='" + grade + '\'' +
                ", courseType=" + courseType +
                '}';
    }
}
