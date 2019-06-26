package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/21.
 */
public class PatriarchNoEndV1CourseListVo {
    private String courseUuid;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String subject;
    private String teacherName;
    private Integer channelStudentId;
    private Integer channelTeacherId;
    private Integer courseType; //课程类型  0:测评课;1:正式课;2:调试课
    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
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

    public Integer getChannelTeacherId() {
        return channelTeacherId;
    }

    public void setChannelTeacherId(Integer channelTeacherId) {
        this.channelTeacherId = channelTeacherId;
    }

    @Override
    public String toString() {
        return "PatriarchNoEndV1CourseListVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", channelStudentId=" + channelStudentId +
                ", channelTeacherId=" + channelTeacherId +
                '}';
    }
}
