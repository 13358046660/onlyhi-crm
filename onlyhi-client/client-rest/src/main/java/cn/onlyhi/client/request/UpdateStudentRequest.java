package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.Range;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public class UpdateStudentRequest extends BaseRequest {
    @Range(min = 0, max = 1, message = "sex参数非法！")
    private Integer sex;
    private Integer age;
    private String grade;
    private String examArea;
    private String subject;

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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
        return "UpdateStudentRequest{" +
                "sex=" + sex +
                ", age=" + age +
                ", grade='" + grade + '\'' +
                ", examArea='" + examArea + '\'' +
                ", subject='" + subject + '\'' +
                "} " + super.toString();
    }
}
