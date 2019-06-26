package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 新名单列表
 * @author shitongtong
 * Created by shitongtong on 2017/08/15
 */
public class LeadsLog extends BasePo {
    private Long id;

    /**
     * 学生名称
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 年级
     */
    private String grade;

    /**
     * 试听科目
     */
    private String subject;

    /**
     * 报名时间
     */
    private Date signupDate;

    /**
     * 方便联系时间
     */
    private String contactTime;

    /**
     * 接通状态
     */
    private String connectStatus;

    /**
     * 渠道ID
     */
    private String channelUuid;

    /**
     * 计划
     */
    private String jh;

    /**
     * 单元
     */
    private String dy;

    /**
     * 关键字
     */
    private String keyNum;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    private String gradeRank;

    private String city;

    private String province;

    private String count;

    private String school;

    private Integer isHaveMic;

    private Integer isHaveHeadset;

    private Integer isHaveComputer;

    private Integer isHavePad;

    private Integer cityId;

    private Integer provinceId;

    private Integer countId;

    private String phoneLocaltion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }

    public String getContactTime() {
        return contactTime;
    }

    public void setContactTime(String contactTime) {
        this.contactTime = contactTime == null ? null : contactTime.trim();
    }

    public String getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(String connectStatus) {
        this.connectStatus = connectStatus == null ? null : connectStatus.trim();
    }

    public String getChannelUuid() {
        return channelUuid;
    }

    public void setChannelUuid(String channelUuid) {
        this.channelUuid = channelUuid == null ? null : channelUuid.trim();
    }

    public String getJh() {
        return jh;
    }

    public void setJh(String jh) {
        this.jh = jh == null ? null : jh.trim();
    }

    public String getDy() {
        return dy;
    }

    public void setDy(String dy) {
        this.dy = dy == null ? null : dy.trim();
    }

    public String getKeyNum() {
        return keyNum;
    }

    public void setKeyNum(String keyNum) {
        this.keyNum = keyNum == null ? null : keyNum.trim();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getGradeRank() {
        return gradeRank;
    }

    public void setGradeRank(String gradeRank) {
        this.gradeRank = gradeRank == null ? null : gradeRank.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count == null ? null : count.trim();
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school == null ? null : school.trim();
    }

    public Integer getIsHaveMic() {
        return isHaveMic;
    }

    public void setIsHaveMic(Integer isHaveMic) {
        this.isHaveMic = isHaveMic;
    }

    public Integer getIsHaveHeadset() {
        return isHaveHeadset;
    }

    public void setIsHaveHeadset(Integer isHaveHeadset) {
        this.isHaveHeadset = isHaveHeadset;
    }

    public Integer getIsHaveComputer() {
        return isHaveComputer;
    }

    public void setIsHaveComputer(Integer isHaveComputer) {
        this.isHaveComputer = isHaveComputer;
    }

    public Integer getIsHavePad() {
        return isHavePad;
    }

    public void setIsHavePad(Integer isHavePad) {
        this.isHavePad = isHavePad;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCountId() {
        return countId;
    }

    public void setCountId(Integer countId) {
        this.countId = countId;
    }

    public String getPhoneLocaltion() {
        return phoneLocaltion;
    }

    public void setPhoneLocaltion(String phoneLocaltion) {
        this.phoneLocaltion = phoneLocaltion == null ? null : phoneLocaltion.trim();
    }
}