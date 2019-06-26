package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/8.
 */
public class ClassAppraisePageVo {
    private String subject;
    private String grade;
    private String teacherName;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    @Override
    public String toString() {
        return "ClassAppraisePageVo{" +
                "subject='" + subject + '\'' +
                ", grade='" + grade + '\'' +
                ", teacherName='" + teacherName + '\'' +
                '}';
    }
}
