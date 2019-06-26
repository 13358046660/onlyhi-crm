package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/11.
 */
public class StudentVo {
    private String uuid;    //leadsUuid
    private String phone;
    private String iconurl;
    private String name;
    private Integer sex;
    private String grade;
    private String examArea;
    private String subject;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getExamArea() {
        return examArea;
    }

    public void setExamArea(String examArea) {
        this.examArea = examArea;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "StudentVo{" +
                "uuid='" + uuid + '\'' +
                ", phone='" + phone + '\'' +
                ", iconurl='" + iconurl + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", grade='" + grade + '\'' +
                ", examArea='" + examArea + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
