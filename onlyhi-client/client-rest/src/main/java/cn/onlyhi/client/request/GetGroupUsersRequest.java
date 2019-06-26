package cn.onlyhi.client.request;

import cn.onlyhi.common.request.PageRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/17.
 */
public class GetGroupUsersRequest extends PageRequest {
    @NotBlank(message = "groupId不能为空！")
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GetGroupUsersRequest{" +
                "groupId='" + groupId + '\'' +
                "} " + super.toString();
    }
}
