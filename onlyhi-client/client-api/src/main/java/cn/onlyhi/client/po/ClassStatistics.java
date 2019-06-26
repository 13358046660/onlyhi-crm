package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 课耗统计表
 * 定时任务统计当天课耗返回实体
 *
 * @author wqz
 * Created by wqz on 2018/10/30
 */
public class ClassStatistics extends BasePo {
    private String roomId;
    private Integer id;
    private String classUuid;
    private String studentName;

    private String grade;

    private String teacherName;

    private String courseDate;

    /**
     * 排课开始时间
     */
    private String startTime;

    /**
     * 排课结束时间
     */
    private String endTime;

    /**
     * 老师首次进入房间时间
     */
    private Date teacherFirstTime;

    /**
     * 老师最后退出房间时间
     */
    private Date teacherLastTime;

    /**
     * 学生首次进入房间时间
     */
    private Date studentFirstTime;

    /**
     * 学生最后退出房间时间
     */
    private Date studentLastTime;

    /**
     * 共同有效时长（分钟）
     */
    private Integer commonLength;

    /**
     * 老师有效时长（分钟）
     */
    private Integer teacherLength;

    /**
     * 学生有效时长（分钟）
     */
    private Integer studentLength;

    /**
     * 房间id
     */
    private String roomUuid;

    /**
     * 老师核查共同时长
     */
    private Short checkLength;

    /**
     * 核查备注
     */
    private String remark;

    /**
     * 核查人姓名
     */
    private String checker;

    /**
     * 核查时间
     */
    private Date checkTime;
    /**
     * 老师时间
     */
    private String teacherTime;
    /**
     * 学生时间
     */
    private String studentTime;

    public String getTeacherTime() {
        return teacherTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setTeacherTime(String teacherTime) {
        this.teacherTime = teacherTime;
    }

    public String getStudentTime() {
        return studentTime;
    }

    public void setStudentTime(String studentTime) {
        this.studentTime = studentTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(String classUuid) {
        this.classUuid = classUuid == null ? null : classUuid.trim();
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName == null ? null : studentName.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName == null ? null : teacherName.trim();
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid == null ? null : roomUuid.trim();
    }

    public Short getCheckLength() {
        return checkLength;
    }

    public void setCheckLength(Short checkLength) {
        this.checkLength = checkLength;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker == null ? null : checker.trim();
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
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

    public Date getTeacherFirstTime() {
        return teacherFirstTime;
    }

    public void setTeacherFirstTime(Date teacherFirstTime) {
        this.teacherFirstTime = teacherFirstTime;
    }

    public Date getTeacherLastTime() {
        return teacherLastTime;
    }

    public void setTeacherLastTime(Date teacherLastTime) {
        this.teacherLastTime = teacherLastTime;
    }

    public Date getStudentFirstTime() {
        return studentFirstTime;
    }

    public void setStudentFirstTime(Date studentFirstTime) {
        this.studentFirstTime = studentFirstTime;
    }

    public Date getStudentLastTime() {
        return studentLastTime;
    }

    public void setStudentLastTime(Date studentLastTime) {
        this.studentLastTime = studentLastTime;
    }

    public Integer getCommonLength() {
        return commonLength;
    }

    public void setCommonLength(Integer commonLength) {
        this.commonLength = commonLength;
    }

    public Integer getTeacherLength() {
        return teacherLength;
    }

    public void setTeacherLength(Integer teacherLength) {
        this.teacherLength = teacherLength;
    }

    public Integer getStudentLength() {
        return studentLength;
    }

    public void setStudentLength(Integer studentLength) {
        this.studentLength = studentLength;
    }

    @Override
    public String toString() {
        return "ClassStatistics{" +
                "roomId='" + roomId + '\'' +
                ", id=" + id +
                ", classUuid='" + classUuid + '\'' +
                ", studentName='" + studentName + '\'' +
                ", grade='" + grade + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", teacherFirstTime=" + teacherFirstTime +
                ", teacherLastTime=" + teacherLastTime +
                ", studentFirstTime=" + studentFirstTime +
                ", studentLastTime=" + studentLastTime +
                ", commonLength=" + commonLength +
                ", teacherLength=" + teacherLength +
                ", studentLength=" + studentLength +
                ", roomUuid='" + roomUuid + '\'' +
                ", checkLength=" + checkLength +
                ", remark='" + remark + '\'' +
                ", checker='" + checker + '\'' +
                ", checkTime=" + checkTime +
                ", teacherTime='" + teacherTime + '\'' +
                ", studentTime='" + studentTime + '\'' +
                '}';
    }
}