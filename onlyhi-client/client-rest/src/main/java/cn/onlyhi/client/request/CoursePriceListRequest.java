package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/25.
 */
public class CoursePriceListRequest extends BaseRequest {
    @NotBlank(message = "activityType不能为空！")
    private String activityType;
    @NotBlank(message = "type不能为空！")
    private String type;

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CoursePriceListRequest{" +
                "activityType='" + activityType + '\'' +
                ", type='" + type + '\'' +
                "} " + super.toString();
    }
}
