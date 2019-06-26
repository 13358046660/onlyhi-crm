package cn.onlyhi.common.request;

import org.hibernate.validator.constraints.Range;

/**
 * 分页请求通用参数封装类
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/5/19.
 */
public class PageRequest extends BaseRequest {

    @Range(min = 1, max = Integer.MAX_VALUE, message = "pageNo范围：1~2147483647")
    private Integer pageNo;
    @Range(min = 1, max = Integer.MAX_VALUE, message = "pageSize范围：1~2147483647")
    private Integer pageSize;

    public Integer getPageNo() {
        return pageNo == null ? 1 : pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                "} " + super.toString();
    }
}
