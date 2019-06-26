package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/18.
 */
public class TeacherCourseRecordVo {
    private String courseUuid;
    private String courseDate;
    /**
     * 实际开始时间
     */
    private String startTime;
    /**
     * 实际结束时间
     */
    private String endTime;
    private String subject;
    private String name;
    private Integer courseType;
    private String classTeacherAppraiseUuid;
    private String grade;
    private String subjectVersion;
    private Integer recordStatus; //1:有回放，2:没有回放数据，3:视频转换中，次日方可查看。
    /**
     * 计划开始时间
     */
    private String planStartTime;
    /**
     * 计划结束时间
     */
    private String planEndTime;
    private List<RoomRecord> roomRecordVo;
    /**
     * 学生与老师有效时长（分钟）
     */
    private Integer commonTime;

    public List<RoomRecord> getRoomRecordVo() {
        return roomRecordVo;
    }

    public void setRoomRecordVo(List<RoomRecord> roomRecordVo) {
        this.roomRecordVo = roomRecordVo;
    }

    public Integer getCommonTime() {
        return commonTime;
    }

    public void setCommonTime(Integer commonTime) {
        this.commonTime = commonTime;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Integer getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getClassTeacherAppraiseUuid() {
        return classTeacherAppraiseUuid;
    }

    public void setClassTeacherAppraiseUuid(String classTeacherAppraiseUuid) {
        this.classTeacherAppraiseUuid = classTeacherAppraiseUuid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubjectVersion() {
        return subjectVersion;
    }

    public void setSubjectVersion(String subjectVersion) {
        this.subjectVersion = subjectVersion;
    }

    @Override
    public String toString() {
        return "TeacherCourseRecordVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", name='" + name + '\'' +
                ", courseType=" + courseType +
                ", classTeacherAppraiseUuid='" + classTeacherAppraiseUuid + '\'' +
                ", grade='" + grade + '\'' +
                ", subjectVersion='" + subjectVersion + '\'' +
                ", recordStatus=" + recordStatus +
                ", planStartTime='" + planStartTime + '\'' +
                ", planEndTime='" + planEndTime + '\'' +
                ", roomRecordVo=" + roomRecordVo +
                ", commonTime=" + commonTime +
                '}';
    }
}
