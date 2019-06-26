package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 课程支付订单表
 * @author shitongtong
 * Created by shitongtong on 2017/08/15
 */
public class CpPayRequest extends BasePo {
    /**
     * 系统id,自增长
     */
    private Integer id;

    /**
     * 付费申请uuid
     */
    private String uuid;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 父订单号（针对子订单来说的）
     */
    private String parentOrderNo;

    /**
     * 课程订单uuid
     */
    private String courseOrderUuid;

    /**
     * 课时包的uuid
     */
    private String coursePriceUuid;

    /**
     * 优惠码uuid
     */
    private String promotionCodeUuid;

    /**
     * 订单审核人
     */
    private String gaUserUuid;

    /**
     * 用户uuid
     */
    private String userUuid;

    private String leadsUuid;

    /**
     * 购买时长
     */
    private String buyLength;

    /**
     * 课程级别0：普通1：清北2:明星
     */
    private Byte courseLevel;

    /**
     * 购买金额(应付款)
     */
    private String money;

    /**
     * 参与的活动
     */
    private String inActivities;

    /**
     * 付费方式: bank:银行支付,taobao:淘宝,alipay:支付宝,weixin:微信,baiduStaging:百度分期,pingpp:ping++支付
     */
    private String payType;

    /**
     * 所选择银行
     */
    private String bank;

    /**
     * P++支付订单号
     */
    private String chargeId;

    /**
     * ping++支付渠道返回的交易流水号
     */
    private String transactionNo;

    /**
     * 已付款
     */
    private Double alreadyPay;

    /**
     * 待付款
     */
    private Double pendingPay;

    /**
     * 付款人姓名
     */
    private String payer;

    /**
     * 付款时间
     */
    private String payTime;

    /**
     * 审核时间
     */
    private Date checkTime;

    /**
     * 是否支付状态0：待审核(待支付)1：支付成功2：支付失败3:已被拆单
     */
    private Byte isPay;

    private String checkReamrk;

    private String realUserUuid;

    /**
     * 官网子订单号
     */
    private String webOrderNo;

    /**
     * 支付请求的渠道0：正常流程渠道1：绿色通道2：官网线下主单
     */
    private Byte payChannel;

    /**
     * 是否被合并0：否1：是
     */
    private Boolean isMerge;

    /**
     * 状态0：禁用1：启用
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
     * 是否被拆分0：否1：是
     */
    private Boolean isSplit;

    /**
     * 剩余时长
     */
    private String residueLength;

    private String teamUuid;

    /**
     * ping++支付渠道
     */
    private String pingppChannel;

    /**
     * 百度分期支付使用的百度编码
     */
    private String baiduCode;

    /**
     * 百度分期支付家长姓名
     */
    private String patriarchName;

    /**
     * 百度分期支付家长手机号
     */
    private String patriarchPhone;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getParentOrderNo() {
        return parentOrderNo;
    }

    public void setParentOrderNo(String parentOrderNo) {
        this.parentOrderNo = parentOrderNo == null ? null : parentOrderNo.trim();
    }

    public String getCourseOrderUuid() {
        return courseOrderUuid;
    }

    public void setCourseOrderUuid(String courseOrderUuid) {
        this.courseOrderUuid = courseOrderUuid == null ? null : courseOrderUuid.trim();
    }

    public String getCoursePriceUuid() {
        return coursePriceUuid;
    }

    public void setCoursePriceUuid(String coursePriceUuid) {
        this.coursePriceUuid = coursePriceUuid == null ? null : coursePriceUuid.trim();
    }

    public String getPromotionCodeUuid() {
        return promotionCodeUuid;
    }

    public void setPromotionCodeUuid(String promotionCodeUuid) {
        this.promotionCodeUuid = promotionCodeUuid == null ? null : promotionCodeUuid.trim();
    }

    public String getGaUserUuid() {
        return gaUserUuid;
    }

    public void setGaUserUuid(String gaUserUuid) {
        this.gaUserUuid = gaUserUuid == null ? null : gaUserUuid.trim();
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

    public String getBuyLength() {
        return buyLength;
    }

    public void setBuyLength(String buyLength) {
        this.buyLength = buyLength == null ? null : buyLength.trim();
    }

    public Byte getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(Byte courseLevel) {
        this.courseLevel = courseLevel;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money == null ? null : money.trim();
    }

    public String getInActivities() {
        return inActivities;
    }

    public void setInActivities(String inActivities) {
        this.inActivities = inActivities == null ? null : inActivities.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank == null ? null : bank.trim();
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId == null ? null : chargeId.trim();
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo == null ? null : transactionNo.trim();
    }

    public Double getAlreadyPay() {
        return alreadyPay;
    }

    public void setAlreadyPay(Double alreadyPay) {
        this.alreadyPay = alreadyPay;
    }

    public Double getPendingPay() {
        return pendingPay;
    }

    public void setPendingPay(Double pendingPay) {
        this.pendingPay = pendingPay;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer == null ? null : payer.trim();
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime == null ? null : payTime.trim();
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Byte getIsPay() {
        return isPay;
    }

    public void setIsPay(Byte isPay) {
        this.isPay = isPay;
    }

    public String getCheckReamrk() {
        return checkReamrk;
    }

    public void setCheckReamrk(String checkReamrk) {
        this.checkReamrk = checkReamrk == null ? null : checkReamrk.trim();
    }

    public String getRealUserUuid() {
        return realUserUuid;
    }

    public void setRealUserUuid(String realUserUuid) {
        this.realUserUuid = realUserUuid == null ? null : realUserUuid.trim();
    }

    public String getWebOrderNo() {
        return webOrderNo;
    }

    public void setWebOrderNo(String webOrderNo) {
        this.webOrderNo = webOrderNo == null ? null : webOrderNo.trim();
    }

    public Byte getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(Byte payChannel) {
        this.payChannel = payChannel;
    }

    public Boolean getIsMerge() {
        return isMerge;
    }

    public void setIsMerge(Boolean isMerge) {
        this.isMerge = isMerge;
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

    public Boolean getIsSplit() {
        return isSplit;
    }

    public void setIsSplit(Boolean isSplit) {
        this.isSplit = isSplit;
    }

    public String getResidueLength() {
        return residueLength;
    }

    public void setResidueLength(String residueLength) {
        this.residueLength = residueLength == null ? null : residueLength.trim();
    }

    public String getTeamUuid() {
        return teamUuid;
    }

    public void setTeamUuid(String teamUuid) {
        this.teamUuid = teamUuid == null ? null : teamUuid.trim();
    }

    public String getPingppChannel() {
        return pingppChannel;
    }

    public void setPingppChannel(String pingppChannel) {
        this.pingppChannel = pingppChannel == null ? null : pingppChannel.trim();
    }

    public String getBaiduCode() {
        return baiduCode;
    }

    public void setBaiduCode(String baiduCode) {
        this.baiduCode = baiduCode == null ? null : baiduCode.trim();
    }

    public String getPatriarchName() {
        return patriarchName;
    }

    public void setPatriarchName(String patriarchName) {
        this.patriarchName = patriarchName == null ? null : patriarchName.trim();
    }

    public String getPatriarchPhone() {
        return patriarchPhone;
    }

    public void setPatriarchPhone(String patriarchPhone) {
        this.patriarchPhone = patriarchPhone == null ? null : patriarchPhone.trim();
    }
}