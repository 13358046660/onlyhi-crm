package cn.onlyhi.client.dto;

import cn.onlyhi.client.po.TcTeacherFile;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/27.
 */
public class SaveTeacherInfoDto extends BaseDto{
    private String teacherUuid;
    private String teacherName;
    private Integer sex; //性别  0女 1 男
    private String phone;
    private String wechat;
    private String qq;
    private String email;
    private String emergencyPhone;
    private String emergencyRelation;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private Integer artsOrScience;  //文科或者理科 1 文科  2 理科
    private String teacherSchoolUuid;  //tc_teacher_school 的uuid tc_teacher表的school_location
    private Integer education;  //学历 1本科  2 研究生
    private Integer highestEducation;
    private String major;
    private String grade;
    private String gradePreferenceValue; //年级偏好 1,"小学"   2,"小学,初中"  3,"小学,初中,高中"  4,"初中"  5,"初中,高中"  6,"高中"
    private String teachingSubjectUuid;    //第一科目uuid
    private String secondSubjectUuid;   //第二科目Uuid
    private String thirdSubjectUuid;    //第三科目Uuid
    private String idNumber;
    private String cardNumber;
    private String bankAddress;
    private List<TcTeacherFile> teacherFileList;

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid;
    }

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

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyRelation() {
        return emergencyRelation;
    }

    public void setEmergencyRelation(String emergencyRelation) {
        this.emergencyRelation = emergencyRelation;
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

    public List<TcTeacherFile> getTeacherFileList() {
        return teacherFileList;
    }

    public void setTeacherFileList(List<TcTeacherFile> teacherFileList) {
        this.teacherFileList = teacherFileList;
    }
}
