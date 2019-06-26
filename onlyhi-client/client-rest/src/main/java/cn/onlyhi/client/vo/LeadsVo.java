package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/18.
 */
public class LeadsVo {
    private String photo;   //头像
    private String name;
    private Integer sex;
    private String phone;
    private String grade;
    private String subject;
    private String address; //地址
    private String school;  //所在学校
    private String schoolLevel; //学校级别
    private String gradeRank;   //年级排名

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getGradeRank() {
        return gradeRank;
    }

    public void setGradeRank(String gradeRank) {
        this.gradeRank = gradeRank;
    }

    @Override
    public String toString() {
        return "LeadsVo{" +
                "photo='" + photo + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", grade='" + grade + '\'' +
                ", subject='" + subject + '\'' +
                ", address='" + address + '\'' +
                ", school='" + school + '\'' +
                ", schoolLevel='" + schoolLevel + '\'' +
                ", gradeRank='" + gradeRank + '\'' +
                '}';
    }
}
