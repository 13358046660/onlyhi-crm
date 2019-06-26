package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/26.
 */
public class CourseLeadsVo {
    private int channelStudentId;
    private String studentName;

    public int getChannelStudentId() {
        return channelStudentId;
    }

    public void setChannelStudentId(int channelStudentId) {
        this.channelStudentId = channelStudentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return "CourseLeadsVo{" +
                "channelStudentId=" + channelStudentId +
                ", studentName='" + studentName + '\'' +
                '}';
    }
}
