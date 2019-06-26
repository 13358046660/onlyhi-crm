package cn.onlyhi.client.request;

import cn.onlyhi.client.vo.FreeTimeVo;
import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/5.
 */
public class SaveFreeTimeRequest extends BaseRequest{
    @NotBlank(message = "空闲年月不能为空！")
    @Pattern(regexp = "^(\\d{4}-\\d{2})$", message = "空闲年月格式错误，正确格式：yyyy-MM")
    private String freeDateTime;    //空闲年月 yyyy-MM
    private List<List<FreeTimeVo>> freeTimeListList;

    public String getFreeDateTime() {
        return freeDateTime;
    }

    public void setFreeDateTime(String freeDateTime) {
        this.freeDateTime = freeDateTime;
    }

    public List<List<FreeTimeVo>> getFreeTimeListList() {
        return freeTimeListList;
    }

    public void setFreeTimeListList(List<List<FreeTimeVo>> freeTimeListList) {
        this.freeTimeListList = freeTimeListList;
    }

    @Override
    public String toString() {
        return "SaveFreeTimeRequest{" +
                "freeDateTime='" + freeDateTime + '\'' +
                ", freeTimeListList=" + freeTimeListList +
                "} " + super.toString();
    }
}
