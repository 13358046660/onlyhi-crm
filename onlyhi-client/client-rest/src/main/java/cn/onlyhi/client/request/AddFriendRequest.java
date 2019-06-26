package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/5.
 */
public class AddFriendRequest extends BaseRequest {
    @NotBlank(message = "ownerUserName不能为空！")
    private String ownerUserName;
    @NotBlank(message = "friendUserName不能为空！")
    private String friendUserName;

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getFriendUserName() {
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    @Override
    public String toString() {
        return "addFriendRequest{" +
                "ownerUserName='" + ownerUserName + '\'' +
                ", friendUserName='" + friendUserName + '\'' +
                "} " + super.toString();
    }
}
