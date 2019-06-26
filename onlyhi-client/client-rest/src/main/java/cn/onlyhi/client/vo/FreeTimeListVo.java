package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/5.
 */
public class FreeTimeListVo {
    private String freeDateTime;    //空闲年月(yyyy-MM)
    private List<FreeTimeVo> freeTimeList;    //一个月的空闲数据

    public String getFreeDateTime() {
        return freeDateTime;
    }

    public void setFreeDateTime(String freeDateTime) {
        this.freeDateTime = freeDateTime;
    }

    public List<FreeTimeVo> getFreeTimeList() {
        return freeTimeList;
    }

    public void setFreeTimeList(List<FreeTimeVo> freeTimeList) {
        this.freeTimeList = freeTimeList;
    }

    @Override
    public String toString() {
        return "FreeTimeListVo{" +
                "freeDateTime='" + freeDateTime + '\'' +
                ", freeTimeList=" + freeTimeList +
                '}';
    }
}
