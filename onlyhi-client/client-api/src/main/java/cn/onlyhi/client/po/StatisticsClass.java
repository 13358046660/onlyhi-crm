package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 统计课时临时表（数据来源sql多表结果集）
 *
 * @author JYDZ3010164
 * Created by JYDZ3010164 on 2018/10/12
 */
public class StatisticsClass extends BasePo {
    /**
     * 课时唯一id
     */
    private String classUuid;

    private String studentName;
    private String grade;

    private String teacherName;

    /**
     * 课程日期
     */
    private String courseDate;

    private String startTime;

    private String endTime;

    /**
     * 老师首次进入房间时间
     */
    private Date teacherEnterTime;

    /**
     * 老师最后退出房间时间
     */
    private Date teacherOutTime;

    /**
     * 学生首次进入房间时间
     */
    private Date studentEnterTime;

    /**
     * 学生最后退出房间时间
     */
    private Date studentOutTime;

    /**
     * 共同在线上课时长
     */
    private Integer commonTime;

    private Integer teacherTime;

    /**
     * 学生在线上课时长
     */
    private Integer studentTime;

    /**
     * 上课房间id
     */
    private String roomUuid;

    /**
     * 备注
     */
    private String remark;
    private String teacherRecordTime;
    private String studentRecordTime;


    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName == null ? null : teacherName.trim();
    }


    public Date getTeacherEnterTime() {
        return teacherEnterTime;
    }

    public void setTeacherEnterTime(Date teacherEnterTime) {
        this.teacherEnterTime = teacherEnterTime;
    }

    public Date getTeacherOutTime() {
        return teacherOutTime;
    }

    public void setTeacherOutTime(Date teacherOutTime) {
        this.teacherOutTime = teacherOutTime;
    }

    public Date getStudentEnterTime() {
        return studentEnterTime;
    }

    public void setStudentEnterTime(Date studentEnterTime) {
        this.studentEnterTime = studentEnterTime;
    }

    public Date getStudentOutTime() {
        return studentOutTime;
    }

    public void setStudentOutTime(Date studentOutTime) {
        this.studentOutTime = studentOutTime;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid == null ? null : roomUuid.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getTeacherRecordTime() {
        return teacherRecordTime;
    }

    public void setTeacherRecordTime(String teacherRecordTime) {
        this.teacherRecordTime = teacherRecordTime;
    }

    public String getStudentRecordTime() {
        return studentRecordTime;
    }

    public void setStudentRecordTime(String studentRecordTime) {
        this.studentRecordTime = studentRecordTime;
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

    @Override
    public String toString() {
        return "StatisticsClass{" +
                "classUuid='" + classUuid + '\'' +
                ", studentName='" + studentName + '\'' +
                ", grade='" + grade + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", teacherEnterTime=" + teacherEnterTime +
                ", teacherOutTime=" + teacherOutTime +
                ", studentEnterTime=" + studentEnterTime +
                ", studentOutTime=" + studentOutTime +
                ", commonTime=" + commonTime +
                ", teacherTime=" + teacherTime +
                ", studentTime=" + studentTime +
                ", roomUuid='" + roomUuid + '\'' +
                ", remark='" + remark + '\'' +
                ", teacherRecordTime='" + teacherRecordTime + '\'' +
                ", studentRecordTime='" + studentRecordTime + '\'' +
                '}';
    }

    public Integer getCommonTime() {
        return commonTime;
    }

    public void setCommonTime(Integer commonTime) {
        this.commonTime = commonTime;
    }

    public Integer getTeacherTime() {
        return teacherTime;
    }

    public void setTeacherTime(Integer teacherTime) {
        this.teacherTime = teacherTime;
    }

    public Integer getStudentTime() {
        return studentTime;
    }

    public void setStudentTime(Integer studentTime) {
        this.studentTime = studentTime;
    }
}