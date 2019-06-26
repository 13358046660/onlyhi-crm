package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/21.
 */
public class TeacherNoStartCourseListVo {
    private String courseUuid;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String subject;
    private String teacherName;
    private List<CourseLeadsVo> studentList;
    private Integer courseType; //课程类型：0：测评课1：正式课 2.调试课

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<CourseLeadsVo> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<CourseLeadsVo> studentList) {
        this.studentList = studentList;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    @Override
    public String toString() {
        return "TeacherNoStartCourseListVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", studentList=" + studentList +
                ", courseType=" + courseType +
                '}';
    }
}
