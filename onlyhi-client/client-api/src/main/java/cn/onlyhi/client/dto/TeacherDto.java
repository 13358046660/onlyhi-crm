package cn.onlyhi.client.dto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/27.
 */
public class TeacherDto extends BaseDto{
    private Long teacherId;
    private String wechat;
    private String qq;
    private String email;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String examLocation;    //高考所在地
    private String teacherSchoolUuid;  //在读院校  学校id 关联表 tc_teacher_school 的uuid
    private String teacherSchoolName;  //在读院校  学校id 关联表 tc_teacher_school 的uuid
    private Integer artsOrScience;  //文科或者理科 1 文科  2 理科
    private Integer education;  //学历 1本科  2 研究生
    private Integer highestEducation;
    private String major;   //专业
    private String grade;   //年级
    private String gradePreferenceValue; //年级偏好 1,"小学"   2,"小学,初中"  3,"小学,初中,高中"    4,"初中"  5,"初中,高中"  6,"高中"
    private String gradePreferenceName; //年级偏好 1,"小学"   2,"小学,初中"  3,"小学,初中,高中"    4,"初中"  5,"初中,高中"  6,"高中"
    private String phone;
    private Integer sex; //性别  0女 1 男
    private String teacherName;
    private String emergencyPhone;
    private String emergencyRelation;
    private String teachingSubjectUuid;
    private String secondSubjectUuid;
    private String thirdSubjectUuid;
    private String teachingSubject;    //第一科目
    private String secondSubject;   //第二科目
    private String thirdSubject;    //第三科目
    private String idNumber;
    private String cardNumber;
    private String bankAddress;

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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
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

    public String getExamLocation() {
        return examLocation;
    }

    public void setExamLocation(String examLocation) {
        this.examLocation = examLocation;
    }

    public Integer getArtsOrScience() {
        return artsOrScience;
    }

    public void setArtsOrScience(Integer artsOrScience) {
        this.artsOrScience = artsOrScience;
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeachingSubject() {
        return teachingSubject;
    }

    public void setTeachingSubject(String teachingSubject) {
        this.teachingSubject = teachingSubject;
    }

    public String getSecondSubject() {
        return secondSubject;
    }

    public void setSecondSubject(String secondSubject) {
        this.secondSubject = secondSubject;
    }

    public String getThirdSubject() {
        return thirdSubject;
    }

    public void setThirdSubject(String thirdSubject) {
        this.thirdSubject = thirdSubject;
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

    public String getTeacherSchoolUuid() {
        return teacherSchoolUuid;
    }

    public void setTeacherSchoolUuid(String teacherSchoolUuid) {
        this.teacherSchoolUuid = teacherSchoolUuid;
    }

    public String getTeacherSchoolName() {
        return teacherSchoolName;
    }

    public void setTeacherSchoolName(String teacherSchoolName) {
        this.teacherSchoolName = teacherSchoolName;
    }

    public String getGradePreferenceValue() {
        return gradePreferenceValue;
    }

    public void setGradePreferenceValue(String gradePreferenceValue) {
        this.gradePreferenceValue = gradePreferenceValue;
    }

    public String getGradePreferenceName() {
        return gradePreferenceName;
    }

    public void setGradePreferenceName(String gradePreferenceName) {
        this.gradePreferenceName = gradePreferenceName;
    }
}
