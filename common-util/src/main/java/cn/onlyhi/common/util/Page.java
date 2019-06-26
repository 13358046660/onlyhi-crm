package cn.onlyhi.common.util;

import java.util.Collections;
import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/21.
 */
public class Page<T> {

    private long total;

    private List<T> list = Collections.emptyList();

    public Page() {
    }

    public Page(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}
