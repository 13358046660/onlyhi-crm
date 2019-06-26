package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/25.
 */
public class DirectPingppPaymentRequest extends BaseRequest {
    @NotBlank(message = "课时包uuid不能为空！")
    private String coursePriceUuid; //课时包uuid
    private String code;    //优惠码
    @NotBlank(message = "channel不能为空！")
    private String channel; //支付渠道

    private String payType; //付费方式 bank:银行支付 taobao:淘宝 alipay:支付宝 weixin:微信 baiduStaging:百度分期
    private String bank;    //所选择银行（如果是银行支付）

    public String getCoursePriceUuid() {
        return coursePriceUuid;
    }

    public void setCoursePriceUuid(String coursePriceUuid) {
        this.coursePriceUuid = coursePriceUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "DirectPingppPaymentRequest{" +
                "coursePriceUuid='" + coursePriceUuid + '\'' +
                ", code='" + code + '\'' +
                ", channel='" + channel + '\'' +
                ", payType='" + payType + '\'' +
                ", bank='" + bank + '\'' +
                "} " + super.toString();
    }
}
