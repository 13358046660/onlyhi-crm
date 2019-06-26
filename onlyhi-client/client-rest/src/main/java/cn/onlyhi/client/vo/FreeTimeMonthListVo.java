package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/8.
 */
public class FreeTimeMonthListVo {
    private List<FreeTimeListListVo> freeTimeMonthList; //三个月的空闲时间数据
    private boolean isCommit;

    public boolean getIsCommit() {
        return isCommit;
    }

    public void setIsCommit(boolean isCommit) {
        this.isCommit = isCommit;
    }

    public List<FreeTimeListListVo> getFreeTimeMonthList() {
        return freeTimeMonthList;
    }

    public void setFreeTimeMonthList(List<FreeTimeListListVo> freeTimeMonthList) {
        this.freeTimeMonthList = freeTimeMonthList;
    }

    @Override
    public String toString() {
        return "FreeTimeMonthListVo{" +
                "freeTimeMonthList=" + freeTimeMonthList +
                ", isCommit=" + isCommit +
                '}';
    }
}
