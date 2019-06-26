package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/7.
 */
public class CrmTokenRequest extends BaseRequest {
    @NotBlank(message = "crmToken不可为空！")
    private String crmToken;

    public String getCrmToken() {
        return crmToken;
    }

    public void setCrmToken(String crmToken) {
        this.crmToken = crmToken;
    }

    @Override
    public String toString() {
        return "CrmTokenRequest{" +
                "crmToken='" + crmToken + '\'' +
                "} " + super.toString();
    }
}
