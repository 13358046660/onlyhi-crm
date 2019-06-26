package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/18.
 */
public class IMUserNamesRequest extends BaseRequest {
    @Size(min = 1,message = "userNames不能为空！")
    private List<String> userNames;

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    @Override
    public String toString() {
        return "IMUserNamesRequest{" +
                "userNames=" + userNames +
                "} " + super.toString();
    }
}
