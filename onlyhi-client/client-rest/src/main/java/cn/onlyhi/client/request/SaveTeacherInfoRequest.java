package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/27.
 */
public class SaveTeacherInfoRequest extends BaseRequest{
    @NotBlank(message = "teacherName不可为空！")
    private String teacherName;
    @NotNull(message = "sex不可为空！")
    private Integer sex; //性别  0女 1 男
    @NotBlank(message = "手机号不能为空！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String phone;
    @NotBlank(message = "wechat不可为空！")
    private String wechat;
    @NotBlank(message = "qq不能为空！")
    @Pattern(regexp = "^\\d+$", message = "qq格式错误！")
    private String qq;
    @NotBlank(message = "email不能为空！")
    @Pattern(regexp = "^\\S+@\\S+$", message = "email格式错误！")
    private String email;
    /*需求变更，不要了
    @NotBlank(message = "手机号不能为空！")
    @Pattern(regexp = "^\\d{11}$", message = "手机号格式错误！")
    private String emergencyPhone;
    @NotBlank(message = "emergencyRelation不能为空！")
    private String emergencyRelation;*/
    @NotBlank(message = "provinceCode不能为空！")
    private String provinceCode;
    @NotBlank(message = "cityCode不能为空！")
    private String cityCode;
    @NotBlank(message = "districtCode不能为空！")
    private String districtCode;
    @NotNull(message = "artsOrScience不能为空！")
    private Integer artsOrScience;  //文科或者理科 1 文科  2 理科
    @NotBlank(message = "teacherSchoolUuid不能为空！")
    private String teacherSchoolUuid;  //tc_teacher_school 的uuid tc_teacher表的school_location
    @NotNull(message = "education不能为空！")
    private Integer education;  //学历 1本科  2 研究生
    @NotNull(message = "highestEducation不能为空！")
    private Integer highestEducation;
    @NotBlank(message = "major不能为空！")
    private String major;
    @NotBlank(message = "grade不能为空！")
    private String grade;
    @NotBlank(message = "gradePreferenceValue不能为空！")
    private String gradePreferenceValue; //年级偏好 1,"小学"   2,"小学,初中"  3,"小学,初中,高中"  4,"初中"  5,"初中,高中"  6,"高中"
    @NotBlank(message = "teachingSubjectUuid不能为空！")
    private String teachingSubjectUuid;    //第一科目uuid
    @NotBlank(message = "secondSubjectUuid不能为空！")
    private String secondSubjectUuid;   //第二科目Uuid
    @NotBlank(message = "thirdSubjectUuid不能为空！")
    private String thirdSubjectUuid;    //第三科目Uuid
    @NotBlank(message = "idNumber不能为空！")
    private String idNumber;
    @NotBlank(message = "cardNumber不能为空！")
    private String cardNumber;
    @NotBlank(message = "bankAddress不能为空！")
    private String bankAddress;
    @Valid
    private List<TeacherFileRequest> teacherFileList;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public Integer getArtsOrScience() {
        return artsOrScience;
    }

    public void setArtsOrScience(Integer artsOrScience) {
        this.artsOrScience = artsOrScience;
    }

    public String getTeacherSchoolUuid() {
        return teacherSchoolUuid;
    }

    public void setTeacherSchoolUuid(String teacherSchoolUuid) {
        this.teacherSchoolUuid = teacherSchoolUuid;
    }

    public Integer getEducation() {
        return education;
    }

    public void setEducation(Integer education) {
        this.education = education;
    }

    public Integer getHighestEducation() {
        return highestEducation;
    }

    public void setHighestEducation(Integer highestEducation) {
        this.highestEducation = highestEducation;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGradePreferenceValue() {
        return gradePreferenceValue;
    }

    public void setGradePreferenceValue(String gradePreferenceValue) {
        this.gradePreferenceValue = gradePreferenceValue;
    }

    public String getTeachingSubjectUuid() {
        return teachingSubjectUuid;
    }

    public void setTeachingSubjectUuid(String teachingSubjectUuid) {
        this.teachingSubjectUuid = teachingSubjectUuid;
    }

    public String getSecondSubjectUuid() {
        return secondSubjectUuid;
    }

    public void setSecondSubjectUuid(String secondSubjectUuid) {
        this.secondSubjectUuid = secondSubjectUuid;
    }

    public String getThirdSubjectUuid() {
        return thirdSubjectUuid;
    }

    public void setThirdSubjectUuid(String thirdSubjectUuid) {
        this.thirdSubjectUuid = thirdSubjectUuid;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public List<TeacherFileRequest> getTeacherFileList() {
        return teacherFileList;
    }

    public void setTeacherFileList(List<TeacherFileRequest> teacherFileList) {
        this.teacherFileList = teacherFileList;
    }

    @Override
    public String toString() {
        return "SaveTeacherInfoRequest{" +
                "teacherName='" + teacherName + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", districtCode='" + districtCode + '\'' +
                ", artsOrScience=" + artsOrScience +
                ", teacherSchoolUuid='" + teacherSchoolUuid + '\'' +
                ", education=" + education +
                ", highestEducation=" + highestEducation +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", gradePreferenceValue='" + gradePreferenceValue + '\'' +
                ", teachingSubjectUuid='" + teachingSubjectUuid + '\'' +
                ", secondSubjectUuid='" + secondSubjectUuid + '\'' +
                ", thirdSubjectUuid='" + thirdSubjectUuid + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", bankAddress='" + bankAddress + '\'' +
                ", teacherFileList=" + teacherFileList +
                "} " + super.toString();
    }
}
