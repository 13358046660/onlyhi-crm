package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 教师表
 * @author shitongtong
 * Created by shitongtong on 2018/01/27
 */
public class TcTeacher extends BasePo {
    private Long id;

    private String uuid;

    /**
     * 培训考试答题的编号
     */
    private String trainingQuestionNumber;

    /**
     * 老师的培训账户ID
     */
    private Long trainingAccountId;

    private String tcName;

    /**
     * 老师微信的openId
     */
    private String openId;

    /**
     * 密码
     */
    private String password;

    /**
     * 性别  0女 1 男
     */
    private Integer sex;

    /**
     * 邀约时间
     */
    private Date invitationDate;

    /**
     * 老师手动更新空闲时间的时间
     */
    private Date updateTime;

    /**
     * 邀约人的UUID
     */
    private String invitationUuid;

    /**
     * 老师的分部
     */
    private String branch;

    /**
     * 面试安排的ID
     */
    private Long interviewScheduleId;

    /**
     * 工作性质    1,"社招全职"  2,"兼职"  3,"校招全职"
     */
    private Integer natureOfWork;

    private String phone;

    private String email;

    /**
     * QQ号码
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 专业
     */
    private String major;

    /**
     * 薪资等级  1 C 2 B 3 A 4 S 5 S+ 6 明星
     */
    private Integer payGrade;

    /**
     * 时薪
     */
    private Integer hourlyWage;

    /**
     * 高考所在地
     */
    private String schoolLocation;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学历 1本科  2 研究生
     */
    private Integer education;

    /**
     * 最高学历  1本科  2 研究生
     */
    private Integer highestEducation;

    /**
     * 省
     */
    private String province;

    /**
     * 市 
     */
    private String city;

    /**
     *  区
     */
    private String district;

    /**
     * 文科或者理科 1 文科  2 理科
     */
    private Integer artsOrScience;

    /**
     * 学科
     */
    private String subject;

    /**
     * 教授学科
     */
    private String teachingSubject;

    /**
     * 第二科目
     */
    private String secondSubject;

    /**
     * 第三科目
     */
    private String thirdSubject;

    /**
     * 年级偏好 1,"小学"   2,"小学,初中"  3,"小学,初中,高中"    4,"初中"  5,"初中,高中"  6,"高中"
     */
    private String gradePreference;

    /**
     * 是否有视频  0 无  1  有
     */
    private Integer haveVideo;

    /**
     * 招师方向 1 兼职 2 公校 3 校招  4  三方
     */
    private Integer agentType;

    /**
     * 招师代理
     */
    private String agentUuid;

    /**
     *  银行ID
     */
    private String bank;

    /**
     * 开户支行
     */
    private String bankAddress;

    /**
     * 银行卡号
     */
    private String cardNumber;

    /**
     * 老师状态 1 待面试 2 待培训 3 待完善资料 4 待二次培训 5 讲师可排课 6不可排课20 回收站
     */
    private Integer teacherStatus;

    /**
     * 是否通过审核 0 未通过 1 已通过
     */
    private Integer isPass;

    /**
     * 备注
     */
    private String remark;

    /**
     *  是否可用 0 可用 1 禁用
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 更新人
     */
    private String updateUserId;

    /**
     * 版本
     */
    private Long version;

    /**
     * 推荐人
     */
    private String referral;

    /**
     * 入库时间
     */
    private Date inventoryDate;

    /**
     * 第一次面试的时间
     */
    private Date interviewDate;

    /**
     * 监课备注
     */
    private String teacherNotes;

    /**
     * 待邀约对老师的备注
     */
    private String registerRemark;

    /**
     * 老师的居住地址
     */
    private String address;

    /**
     * 紧急联系人电话
     */
    private String emergencyPhone;

    /**
     * 与紧急联系人的关系
     */
    private String emergencyRelation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getTrainingQuestionNumber() {
        return trainingQuestionNumber;
    }

    public void setTrainingQuestionNumber(String trainingQuestionNumber) {
        this.trainingQuestionNumber = trainingQuestionNumber == null ? null : trainingQuestionNumber.trim();
    }

    public Long getTrainingAccountId() {
        return trainingAccountId;
    }

    public void setTrainingAccountId(Long trainingAccountId) {
        this.trainingAccountId = trainingAccountId;
    }

    public String getTcName() {
        return tcName;
    }

    public void setTcName(String tcName) {
        this.tcName = tcName == null ? null : tcName.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getInvitationDate() {
        return invitationDate;
    }

    public void setInvitationDate(Date invitationDate) {
        this.invitationDate = invitationDate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getInvitationUuid() {
        return invitationUuid;
    }

    public void setInvitationUuid(String invitationUuid) {
        this.invitationUuid = invitationUuid == null ? null : invitationUuid.trim();
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch == null ? null : branch.trim();
    }

    public Long getInterviewScheduleId() {
        return interviewScheduleId;
    }

    public void setInterviewScheduleId(Long interviewScheduleId) {
        this.interviewScheduleId = interviewScheduleId;
    }

    public Integer getNatureOfWork() {
        return natureOfWork;
    }

    public void setNatureOfWork(Integer natureOfWork) {
        this.natureOfWork = natureOfWork;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber == null ? null : idNumber.trim();
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major == null ? null : major.trim();
    }

    public Integer getPayGrade() {
        return payGrade;
    }

    public void setPayGrade(Integer payGrade) {
        this.payGrade = payGrade;
    }

    public Integer getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(Integer hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getSchoolLocation() {
        return schoolLocation;
    }

    public void setSchoolLocation(String schoolLocation) {
        this.schoolLocation = schoolLocation == null ? null : schoolLocation.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public Integer getArtsOrScience() {
        return artsOrScience;
    }

    public void setArtsOrScience(Integer artsOrScience) {
        this.artsOrScience = artsOrScience;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getTeachingSubject() {
        return teachingSubject;
    }

    public void setTeachingSubject(String teachingSubject) {
        this.teachingSubject = teachingSubject == null ? null : teachingSubject.trim();
    }

    public String getSecondSubject() {
        return secondSubject;
    }

    public void setSecondSubject(String secondSubject) {
        this.secondSubject = secondSubject == null ? null : secondSubject.trim();
    }

    public String getThirdSubject() {
        return thirdSubject;
    }

    public void setThirdSubject(String thirdSubject) {
        this.thirdSubject = thirdSubject == null ? null : thirdSubject.trim();
    }

    public String getGradePreference() {
        return gradePreference;
    }

    public void setGradePreference(String gradePreference) {
        this.gradePreference = gradePreference == null ? null : gradePreference.trim();
    }

    public Integer getHaveVideo() {
        return haveVideo;
    }

    public void setHaveVideo(Integer haveVideo) {
        this.haveVideo = haveVideo;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid == null ? null : agentUuid.trim();
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank == null ? null : bank.trim();
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress == null ? null : bankAddress.trim();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber == null ? null : cardNumber.trim();
    }

    public Integer getTeacherStatus() {
        return teacherStatus;
    }

    public void setTeacherStatus(Integer teacherStatus) {
        this.teacherStatus = teacherStatus;
    }

    public Integer getIsPass() {
        return isPass;
    }

    public void setIsPass(Integer isPass) {
        this.isPass = isPass;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId == null ? null : updateUserId.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral == null ? null : referral.trim();
    }

    public Date getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(Date inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public Date getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(Date interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getTeacherNotes() {
        return teacherNotes;
    }

    public void setTeacherNotes(String teacherNotes) {
        this.teacherNotes = teacherNotes == null ? null : teacherNotes.trim();
    }

    public String getRegisterRemark() {
        return registerRemark;
    }

    public void setRegisterRemark(String registerRemark) {
        this.registerRemark = registerRemark == null ? null : registerRemark.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone == null ? null : emergencyPhone.trim();
    }

    public String getEmergencyRelation() {
        return emergencyRelation;
    }

    public void setEmergencyRelation(String emergencyRelation) {
        this.emergencyRelation = emergencyRelation == null ? null : emergencyRelation.trim();
    }
}