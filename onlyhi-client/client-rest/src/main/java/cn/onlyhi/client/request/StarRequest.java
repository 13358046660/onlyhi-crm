package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/8.
 */
public class StarRequest extends BaseRequest {
    @NotNull(message = "star不能为空！")
    @Range(min = 1, max = 5, message = "star参数非法！")
    private Integer star;

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "StarRequest{" +
                "star=" + star +
                "} " + super.toString();
    }
}
