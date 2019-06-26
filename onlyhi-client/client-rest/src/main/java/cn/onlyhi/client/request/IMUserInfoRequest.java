package cn.onlyhi.client.request;

import cn.onlyhi.common.request.PageRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/5.
 */
public class IMUserInfoRequest extends PageRequest {
    @NotBlank(message = "userName不能为空！")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "IMUserInfoRequest{" +
                "userName='" + userName + '\'' +
                "} " + super.toString();
    }
}
