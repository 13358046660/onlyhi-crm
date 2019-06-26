package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/25.
 */
public class DateTimeRequest extends BaseRequest {
    @NotBlank(message = "日期不可为空！")
    @Pattern(regexp = "^(\\d{4}-\\d{2})$", message = "日期格式错误，正确格式：yyyy-MM")
    private String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "DateTimeRequest{" +
                "dateTime='" + dateTime + '\'' +
                "} " + super.toString();
    }
}
