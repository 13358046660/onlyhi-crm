package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/9.
 */
public class ViewAppraiseRequest extends BaseRequest {
    @NotBlank(message = "classAppraiseUuid不能为空！")
    private String classAppraiseUuid;   //上课评价uuid，若为空则未评价

    public String getClassAppraiseUuid() {
        return classAppraiseUuid;
    }

    public void setClassAppraiseUuid(String classAppraiseUuid) {
        this.classAppraiseUuid = classAppraiseUuid;
    }

    @Override
    public String toString() {
        return "ViewAppraiseRequest{" +
                "classAppraiseUuid='" + classAppraiseUuid + '\'' +
                "} " + super.toString();
    }
}
