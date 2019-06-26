package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/21.
 */
public class TeacherNoEndCourseVo {
    private String courseUuid;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String subject;
    private String studentName;
    private Integer courseType; //课程类型：0：测评课 1：正式课 2.调试课
    private Integer studentSex; //学生性别  0：男，1：女
    private String subjectVersion;  //教材版本
    private String studyStatus; //学习情况
    private String grade;
    private String monthPoint;  //月考试听知识点
    private String termPoint;  //期中期末试听点
    private String otherPoint;  //其他知识试听点
    private Integer classStatus;    //课程状态：0：课程未完成，1：课程已完成，2：课程进行中  || 进入房间状态  0:未进入过房间  2:进入过房间

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
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

    public Integer getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(Integer studentSex) {
        this.studentSex = studentSex;
    }

    public String getSubjectVersion() {
        return subjectVersion;
    }

    public void setSubjectVersion(String subjectVersion) {
        this.subjectVersion = subjectVersion;
    }

    public String getStudyStatus() {
        return studyStatus;
    }

    public void setStudyStatus(String studyStatus) {
        this.studyStatus = studyStatus;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMonthPoint() {
        return monthPoint;
    }

    public void setMonthPoint(String monthPoint) {
        this.monthPoint = monthPoint;
    }

    public String getTermPoint() {
        return termPoint;
    }

    public void setTermPoint(String termPoint) {
        this.termPoint = termPoint;
    }

    public String getOtherPoint() {
        return otherPoint;
    }

    public void setOtherPoint(String otherPoint) {
        this.otherPoint = otherPoint;
    }

    @Override
    public String toString() {
        return "TeacherNoEndCourseVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseType=" + courseType +
                ", studentSex=" + studentSex +
                ", subjectVersion='" + subjectVersion + '\'' +
                ", studyStatus='" + studyStatus + '\'' +
                ", grade='" + grade + '\'' +
                ", monthPoint='" + monthPoint + '\'' +
                ", termPoint='" + termPoint + '\'' +
                ", otherPoint='" + otherPoint + '\'' +
                ", classStatus=" + classStatus +
                '}';
    }
}
