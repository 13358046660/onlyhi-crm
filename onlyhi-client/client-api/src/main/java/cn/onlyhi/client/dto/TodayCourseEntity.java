package cn.onlyhi.client.dto;

/**
 * @Author wqz
 * <p>客户端-员工技术支持登陆后，返回今天所有待上课程，包括待上课程和正在上课的课程实体信息
 * Created by wqz on 2018/5/4
 */
public class TodayCourseEntity extends BaseDto {
    /**
     * 课程id
     */
    private Integer id;
    /**
     * 课程uuid
     */
    private String uuid;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 上课日期
     */
    private String courseDate;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 老师姓名
     */
    private String teacherName;
    /**
     * 科目名称
     */
    private String subject;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 课程类型
     */

    private Integer courseType;
    /**
     * 年级
     */
    private String grade;
    /**
     * 课程uuid
     */
    private String courseUuid;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }
}
