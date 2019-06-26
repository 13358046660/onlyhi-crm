package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/5.
 */
public class SaveFreeTimeListRequest extends BaseRequest {
    @Valid
    private List<SaveFreeTimeRequest> freeTimeSaveList;
    private List<String> freeDateTimeList;  //6周编辑过的月份集合（元素格式yyyy-MM）

    public List<SaveFreeTimeRequest> getFreeTimeSaveList() {
        return freeTimeSaveList;
    }

    public void setFreeTimeSaveList(List<SaveFreeTimeRequest> freeTimeSaveList) {
        this.freeTimeSaveList = freeTimeSaveList;
    }

    public List<String> getFreeDateTimeList() {
        return freeDateTimeList;
    }

    public void setFreeDateTimeList(List<String> freeDateTimeList) {
        this.freeDateTimeList = freeDateTimeList;
    }

    @Override
    public String toString() {
        return "SaveFreeTimeListRequest{" +
                "freeTimeSaveList=" + freeTimeSaveList +
                ", freeDateTimeList=" + freeDateTimeList +
                "} " + super.toString();
    }
}
