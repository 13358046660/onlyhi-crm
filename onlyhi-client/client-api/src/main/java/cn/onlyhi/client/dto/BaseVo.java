package cn.onlyhi.client.dto;

import java.io.Serializable;

/**
 * 基础入参
 * @Author wqz
 * <p>
 * Created by wqz on 2018/5/4.
 */
public class BaseVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户token验证
     */
    private String token;
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     *起始数量
     */
    private Integer startSize;
    public Integer getStartSize() {
        return startSize;
    }

    public void setStartSize(Integer startSize) {
        this.startSize = startSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
