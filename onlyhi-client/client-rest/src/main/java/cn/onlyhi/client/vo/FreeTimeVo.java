package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/12.
 */
public class FreeTimeVo {
    private String freetimeDate;    //空闲日期
    private List<String> freetimePeriodList;    //空闲时间段 -> 空闲开始时间

    public String getFreetimeDate() {
        return freetimeDate;
    }

    public void setFreetimeDate(String freetimeDate) {
        this.freetimeDate = freetimeDate;
    }

    public List<String> getFreetimePeriodList() {
        return freetimePeriodList;
    }

    public void setFreetimePeriodList(List<String> freetimePeriodList) {
        this.freetimePeriodList = freetimePeriodList;
    }

    @Override
    public String toString() {
        return "FreeTimeVo{" +
                "freetimeDate='" + freetimeDate + '\'' +
                ", freetimePeriodList=" + freetimePeriodList +
                '}';
    }
}
