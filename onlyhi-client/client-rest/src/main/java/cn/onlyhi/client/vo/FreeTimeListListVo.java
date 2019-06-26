package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/12.
 */
public class FreeTimeListListVo {
    private String freeDateTime;    //空闲年月(yyyy-MM)
    private List<List<FreeTimeVo>> freeTimeListList;    //六周的空闲数据（下分六个周）

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
        return "FreeTimeListListVo{" +
                "freeDateTime='" + freeDateTime + '\'' +
                ", freeTimeListList=" + freeTimeListList +
                '}';
    }
}
