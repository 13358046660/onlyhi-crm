package cn.onlyhi.common.request;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/18.
 */
public class BaseRequest {
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
     * php用户token验证
     */
    private String phpCacheToken;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPhpCacheToken() {
        return phpCacheToken;
    }

    public void setPhpCacheToken(String phpCacheToken) {
        this.phpCacheToken = phpCacheToken;
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

    @Override
    public String toString() {
        return "BaseRequest{" +
                "token='" + token + '\'' +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", phpCacheToken='" + phpCacheToken + '\'' +
                '}';
    }
}
