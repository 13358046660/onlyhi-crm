package cn.onlyhi.client.po;

import java.util.Date;

/**
 * @author shitongtong
 * Created by shitongtong on 2017/10/19
 */
public class PromotionCode extends BasePo {
    private Integer id;

    /**
     * 优惠uuid
     */
    private String uuid;

    /**
     * 课时包uuid
     */
    private String coursePriceUuid;

    /**
     * 优惠码
     */
    private String promotionCode;

    /**
     * 优惠名称
     */
    private String promotionName;

    /**
     * 优惠类型0：奖学金活动1：满减活动2：红包活动3:5月优惠4：学习基金
     */
    private Byte promotionType;

    /**
     * 满减活动中满减金额
     */
    private Double fullMoney;

    /**
     * 红包口令
     */
    private String redPassword;

    /**
     * 优惠金额
     */
    private Double promotionMoney;

    /**
     * 创建者
     */
    private String creater;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 状态0：禁用1：启用
     */
    private Boolean status;

    /**
     * 赠送课时
     */
    private Integer giveLength;

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

    public String getCoursePriceUuid() {
        return coursePriceUuid;
    }

    public void setCoursePriceUuid(String coursePriceUuid) {
        this.coursePriceUuid = coursePriceUuid == null ? null : coursePriceUuid.trim();
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode == null ? null : promotionCode.trim();
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName == null ? null : promotionName.trim();
    }

    public Byte getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Byte promotionType) {
        this.promotionType = promotionType;
    }

    public Double getFullMoney() {
        return fullMoney;
    }

    public void setFullMoney(Double fullMoney) {
        this.fullMoney = fullMoney;
    }

    public String getRedPassword() {
        return redPassword;
    }

    public void setRedPassword(String redPassword) {
        this.redPassword = redPassword == null ? null : redPassword.trim();
    }

    public Double getPromotionMoney() {
        return promotionMoney;
    }

    public void setPromotionMoney(Double promotionMoney) {
        this.promotionMoney = promotionMoney;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater == null ? null : updater.trim();
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getGiveLength() {
        return giveLength;
    }

    public void setGiveLength(Integer giveLength) {
        this.giveLength = giveLength;
    }
}