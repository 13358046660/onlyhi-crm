package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/22.
 */
public class UpdatePasswordRequest extends BaseRequest {
    @NotBlank(message = "旧密码不能为空！")
    private String oldPassword; //二次加密后的
    @NotBlank(message = "新密码不能为空!")
    private String newPassword; //一次加密后的
    @NotBlank(message = "时间戳不能为空！")
    private String timestamp;   //加密时的时间戳

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UpdatePasswordRequest{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", timestamp='" + timestamp + '\'' +
                "} " + super.toString();
    }
}
