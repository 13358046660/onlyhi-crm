package cn.onlyhi.client.po;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shitongtong
 * Created by shitongtong on 2017/08/15
 */
public class CpCoursePrice extends BasePo {
    /**
     * 系统id号 自增长
     */
    private Integer id;

    /**
     * 课程包唯一uuid
     */
    private String uuid;

    /**
     * 百度课程编码
     */
    private String baiduCourseCode;

    /**
     * 淘宝链接
     */
    private String taobaoLink;

    /**
     * 课程包级别0：普通1：清北2：明星
     */
    private String level;

    /**
     * 课程包类型
     */
    private String type;

    /**
     * 课时包时长
     */
    private Integer length;

    /**
     * 原单价
     */
    private Long originalUnitPrice;

    /**
     * 原价
     */
    private Long originalPrice;

    /**
     * 现单价
     */
    private Long nowUnitPrice;

    /**
     * 现价
     */
    private Long nowPrice;

    /**
     * 优惠折扣
     */
    private BigDecimal specialDiscount;

    /**
     * 优惠金额
     */
    private Long specialPrice;

    /**
     * 发布上线状态0：未发布1：已上线2：下线
     */
    private Byte onlineStatus;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 描述(课时包对应的赠送描述）
     */
    private String description;

    private String activityType;

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

    public String getBaiduCourseCode() {
        return baiduCourseCode;
    }

    public void setBaiduCourseCode(String baiduCourseCode) {
        this.baiduCourseCode = baiduCourseCode == null ? null : baiduCourseCode.trim();
    }

    public String getTaobaoLink() {
        return taobaoLink;
    }

    public void setTaobaoLink(String taobaoLink) {
        this.taobaoLink = taobaoLink == null ? null : taobaoLink.trim();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level == null ? null : level.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Long getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(Long originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getNowUnitPrice() {
        return nowUnitPrice;
    }

    public void setNowUnitPrice(Long nowUnitPrice) {
        this.nowUnitPrice = nowUnitPrice;
    }

    public Long getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(Long nowPrice) {
        this.nowPrice = nowPrice;
    }

    public BigDecimal getSpecialDiscount() {
        return specialDiscount;
    }

    public void setSpecialDiscount(BigDecimal specialDiscount) {
        this.specialDiscount = specialDiscount;
    }

    public Long getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(Long specialPrice) {
        this.specialPrice = specialPrice;
    }

    public Byte getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Byte onlineStatus) {
        this.onlineStatus = onlineStatus;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType == null ? null : activityType.trim();
    }
}