package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/21.
 */
public class TeacherNoEndV1CourseListVo {
    private String courseUuid;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String subject;
    private String teacherName;
    private Integer channelStudentId;
    private Integer channelPatriarchId;
    private String studentName;
    private Integer courseType; //课程类型：0：测评课1：正式课 2.调试课
    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

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

    public Integer getChannelStudentId() {
        return channelStudentId;
    }

    public void setChannelStudentId(Integer channelStudentId) {
        this.channelStudentId = channelStudentId;
    }

    public Integer getChannelPatriarchId() {
        return channelPatriarchId;
    }

    public void setChannelPatriarchId(Integer channelPatriarchId) {
        this.channelPatriarchId = channelPatriarchId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    @Override
    public String toString() {
        return "TeacherNoEndV1CourseListVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", channelStudentId=" + channelStudentId +
                ", channelPatriarchId=" + channelPatriarchId +
                ", studentName='" + studentName + '\'' +
                ", courseType=" + courseType +
                ", grade='" + grade + '\'' +
                '}';
    }
}
