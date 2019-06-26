package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/29.
 */
public class TeacherSchoolVo {
    private String teacherSchoolUuid;
    private String teacherSchoolName;

    public String getTeacherSchoolName() {
        return teacherSchoolName;
    }

    public void setTeacherSchoolName(String teacherSchoolName) {
        this.teacherSchoolName = teacherSchoolName;
    }

    public String getTeacherSchoolUuid() {
        return teacherSchoolUuid;
    }

    public void setTeacherSchoolUuid(String teacherSchoolUuid) {
        this.teacherSchoolUuid = teacherSchoolUuid;
    }

    @Override
    public String toString() {
        return "TeacherSchoolVo{" +
                "teacherSchoolUuid='" + teacherSchoolUuid + '\'' +
                ", teacherSchoolName='" + teacherSchoolName + '\'' +
                '}';
    }
}
