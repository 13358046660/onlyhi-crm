package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/25.
 */
public class CoursePriceTypeListRequest extends BaseRequest {
    @NotBlank(message = "activityType不能为空！")
    private String activityType;

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    @Override
    public String toString() {
        return "CoursePriceTypeListRequest{" +
                "activityType='" + activityType + '\'' +
                "} " + super.toString();
    }
}
