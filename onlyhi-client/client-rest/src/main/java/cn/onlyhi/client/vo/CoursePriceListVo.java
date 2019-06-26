package cn.onlyhi.client.vo;

import java.math.BigDecimal;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/25.
 */
public class CoursePriceListVo {

    /**
     * 课程包唯一uuid
     */
    private String uuid;

    /**
     * 课时包时长
     */
    private Integer length;

    /**
     * 原价
     */
    private Long originalPrice;

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
     * 课时包名称（type+length+课时）
     */
    private String coursePricePackageName;

    /**
     * 赠送课时
     */
    private Integer giveLength;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
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

    public String getCoursePricePackageName() {
        return coursePricePackageName;
    }

    public void setCoursePricePackageName(String coursePricePackageName) {
        this.coursePricePackageName = coursePricePackageName;
    }

    public Integer getGiveLength() {
        return giveLength;
    }

    public void setGiveLength(Integer giveLength) {
        this.giveLength = giveLength;
    }

    @Override
    public String toString() {
        return "CoursePriceListVo{" +
                "uuid='" + uuid + '\'' +
                ", length=" + length +
                ", originalPrice=" + originalPrice +
                ", nowPrice=" + nowPrice +
                ", specialDiscount=" + specialDiscount +
                ", specialPrice=" + specialPrice +
                ", coursePricePackageName='" + coursePricePackageName + '\'' +
                ", giveLength=" + giveLength +
                '}';
    }
}
