package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2019/5/4
 */
public class DataVO {
    private String total;
    private List<Object> list;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "DataVO{" +
                "total='" + total + '\'' +
                ", list=" + list +
                '}';
    }
}
