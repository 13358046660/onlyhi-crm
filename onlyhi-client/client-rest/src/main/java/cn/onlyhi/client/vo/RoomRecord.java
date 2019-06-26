package cn.onlyhi.client.vo;

/**
 * @Author wqz
 * <p> 老师端回放列表，当前课程老师与进出房间记录
 * Created by wangqianzhi on 2018/10/26.
 */
public class RoomRecord {
    /**
     * 老师进入时间
     */
    private String teacherEnterTime;
    /**
     * 老师退出时间
     */
    private String teacherOuterTime;
    /**
     * 当前进出时间的时长
     */
    private Integer teacherTime;
    /**
     * 学生进入时间
     */
    private String studentEnterTime;
    /**
     * 学生退出时间
     */
    private String studentOuterTime;
    /**
     * 当前进出时间的时长
     */
    private Integer studentTime;
    /**
     * 共同有效时长
     */
    private Integer commonTime;

    public Integer getCommonTime() {
        return commonTime;
    }

    public void setCommonTime(Integer commonTime) {
        this.commonTime = commonTime;
    }

    public String getTeacherEnterTime() {
        return teacherEnterTime;
    }

    public void setTeacherEnterTime(String teacherEnterTime) {
        this.teacherEnterTime = teacherEnterTime;
    }

    public String getTeacherOuterTime() {
        return teacherOuterTime;
    }

    public void setTeacherOuterTime(String teacherOuterTime) {
        this.teacherOuterTime = teacherOuterTime;
    }

    public String getStudentEnterTime() {
        return studentEnterTime;
    }

    public void setStudentEnterTime(String studentEnterTime) {
        this.studentEnterTime = studentEnterTime;
    }

    public String getStudentOuterTime() {
        return studentOuterTime;
    }

    public void setStudentOuterTime(String studentOuterTime) {
        this.studentOuterTime = studentOuterTime;
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

    @Override
    public String toString() {
        return "RoomRecordVo{" +
                "teacherEnterTime='" + teacherEnterTime + '\'' +
                ", teacherOuterTime='" + teacherOuterTime + '\'' +
                ", teacherTime=" + teacherTime +
                ", studentEnterTime='" + studentEnterTime + '\'' +
                ", studentOuterTime='" + studentOuterTime + '\'' +
                ", studentTime=" + studentTime +
                ", commonTime=" + commonTime +
                '}';
    }
}
