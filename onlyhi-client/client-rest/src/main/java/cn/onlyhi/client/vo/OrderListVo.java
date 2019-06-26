package cn.onlyhi.client.vo;

import cn.onlyhi.client.dto.BaseDto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/26.
 */
public class OrderListVo extends BaseDto{
    private String orderUuid;
    private String orderNo;
    private String money;   //应支付金额
    private String createDate;    //创建时间    格式（yyyy-MM-dd HH:mm:ss）
    private String payTime;     //支付时间      格式（yyyy-MM-dd HH:mm:ss）
    private String coursePricePackageName; //课时包名称（type+length+"课时"）
    private Integer orderStatus;    //0:已关闭; 1:待支付; 2:已支付
    private String originalPrice;  //原价
    private String discountPrice;   //优惠金额（原价 - 应支付金额）

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getCoursePricePackageName() {
        return coursePricePackageName;
    }

    public void setCoursePricePackageName(String coursePricePackageName) {
        this.coursePricePackageName = coursePricePackageName;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    @Override
    public String toString() {
        return "OrderListVo{" +
                "orderUuid='" + orderUuid + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", money='" + money + '\'' +
                ", createDate='" + createDate + '\'' +
                ", payTime='" + payTime + '\'' +
                ", coursePricePackageName='" + coursePricePackageName + '\'' +
                ", orderStatus=" + orderStatus +
                ", originalPrice='" + originalPrice + '\'' +
                ", discountPrice='" + discountPrice + '\'' +
                "} " + super.toString();
    }
}
