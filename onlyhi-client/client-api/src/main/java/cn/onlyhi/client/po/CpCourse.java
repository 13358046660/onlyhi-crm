package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 开课记录表
 * @author shitongtong
 * Created by shitongtong on 2018/01/23
 */
public class CpCourse extends BasePo {
    private Integer id;

    /**
     * uuid唯一标识符
     */
    private String uuid;

    /**
     * 消耗课程订单uuid
     */
    private String courseOrderUuid;

    /**
     * 课程请求uuid
     */
    private String courseRequestUuid;

    /**
     * 付费申请uuid或者官网支付订单号
     */
    private String payUuid;

    /**
     * 开课者
     */
    private String userUuid;

    /**
     * leadsUuid
     */
    private String leadsUuid;

    /**
     * 课程类型：0：测评课1：正式课2.调试课
     */
    private Integer courseType;

    /**
     * 上课时长
     */
    private String length;

    /**
     * 上课日期
     */
    private String courseDate;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 开课是否成功   0：失败   1：成功    2:待反馈（调试中）  3:异常    4：已处理
     */
    private Integer isSuccess;

    /**
     * 状态0:被撤销1:未被撤销
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 老师的uuid
     */
    private String teacherUuid;

    private String remark;

    /**
     * 实际开始时间
     */
    private String realStartTime;

    /**
     * 实际结束时间
     */
    private String realEndTime;

    /**
     * 课程对应的销售uuid
     */
    private String saleUserUuid;

    /**
     * 教师课程状态：0：课程未完成，1：课程已完成，2：课程进行中
     */
    private Integer classStatus;

    /**
     * 学生课程状态：0：课程未完成，1：课程已完成，2：课程进行中
     */
    private Integer studentClassStatus;

    /**
     * cc课程状态：0：课程未完成，1：课程已完成，2：课程进行中
     */
    private Integer ccClassStatus;

    /**
     * cr课程状态：0：课程未完成，1：课程已完成，2：课程进行中
     */
    private Integer crClassStatus;

    private Integer pushStatus;

    private Integer hstRoomId;

    private Integer isSendMessage;

    /**
     * 开课类型  1:一对一, 2:一对多
     */
    private Integer classType;

    /**
     * 实际上课时长
     */
    private Integer realLength;

    /**
     * 课程调整类型
1.测评课时间调整;
2.测评课取消;
3.正式课取消;
4.正式课时间调整;
5.测评课调整老师;
6.正式课调整老师;
7.正式课补课
     */
    private Integer reasonForTuning;

    /**
     * 原始课程的UUID
     */
    private String rootUuid;

    /**
     * 上级课程的UUID
     */
    private String parentUuid;

    /**
     * 学生上课时长
     */
    private Integer leadsLength;

    /**
     * 老师上课时长
     */
    private Integer teacherLength;

    /**
     * 扣除上课时长
     */
    private Integer castLength;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getCourseOrderUuid() {
        return courseOrderUuid;
    }

    public void setCourseOrderUuid(String courseOrderUuid) {
        this.courseOrderUuid = courseOrderUuid == null ? null : courseOrderUuid.trim();
    }

    public String getCourseRequestUuid() {
        return courseRequestUuid;
    }

    public void setCourseRequestUuid(String courseRequestUuid) {
        this.courseRequestUuid = courseRequestUuid == null ? null : courseRequestUuid.trim();
    }

    public String getPayUuid() {
        return payUuid;
    }

    public void setPayUuid(String payUuid) {
        this.payUuid = payUuid == null ? null : payUuid.trim();
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid == null ? null : userUuid.trim();
    }

    public String getLeadsUuid() {
        return leadsUuid;
    }

    public void setLeadsUuid(String leadsUuid) {
        this.leadsUuid = leadsUuid == null ? null : leadsUuid.trim();
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length == null ? null : length.trim();
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate == null ? null : courseDate.trim();
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
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

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid == null ? null : teacherUuid.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getRealStartTime() {
        return realStartTime;
    }

    public void setRealStartTime(String realStartTime) {
        this.realStartTime = realStartTime == null ? null : realStartTime.trim();
    }

    public String getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(String realEndTime) {
        this.realEndTime = realEndTime == null ? null : realEndTime.trim();
    }

    public String getSaleUserUuid() {
        return saleUserUuid;
    }

    public void setSaleUserUuid(String saleUserUuid) {
        this.saleUserUuid = saleUserUuid == null ? null : saleUserUuid.trim();
    }

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
    }

    public Integer getStudentClassStatus() {
        return studentClassStatus;
    }

    public void setStudentClassStatus(Integer studentClassStatus) {
        this.studentClassStatus = studentClassStatus;
    }

    public Integer getCcClassStatus() {
        return ccClassStatus;
    }

    public void setCcClassStatus(Integer ccClassStatus) {
        this.ccClassStatus = ccClassStatus;
    }

    public Integer getCrClassStatus() {
        return crClassStatus;
    }

    public void setCrClassStatus(Integer crClassStatus) {
        this.crClassStatus = crClassStatus;
    }

    public Integer getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(Integer pushStatus) {
        this.pushStatus = pushStatus;
    }

    public Integer getHstRoomId() {
        return hstRoomId;
    }

    public void setHstRoomId(Integer hstRoomId) {
        this.hstRoomId = hstRoomId;
    }

    public Integer getIsSendMessage() {
        return isSendMessage;
    }

    public void setIsSendMessage(Integer isSendMessage) {
        this.isSendMessage = isSendMessage;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public Integer getRealLength() {
        return realLength;
    }

    public void setRealLength(Integer realLength) {
        this.realLength = realLength;
    }

    public Integer getReasonForTuning() {
        return reasonForTuning;
    }

    public void setReasonForTuning(Integer reasonForTuning) {
        this.reasonForTuning = reasonForTuning;
    }

    public String getRootUuid() {
        return rootUuid;
    }

    public void setRootUuid(String rootUuid) {
        this.rootUuid = rootUuid == null ? null : rootUuid.trim();
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid == null ? null : parentUuid.trim();
    }

    public Integer getLeadsLength() {
        return leadsLength;
    }

    public void setLeadsLength(Integer leadsLength) {
        this.leadsLength = leadsLength;
    }

    public Integer getTeacherLength() {
        return teacherLength;
    }

    public void setTeacherLength(Integer teacherLength) {
        this.teacherLength = teacherLength;
    }

    public Integer getCastLength() {
        return castLength;
    }

    public void setCastLength(Integer castLength) {
        this.castLength = castLength;
    }
}