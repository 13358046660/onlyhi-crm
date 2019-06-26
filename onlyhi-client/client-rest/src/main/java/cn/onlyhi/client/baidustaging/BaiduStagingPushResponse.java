package cn.onlyhi.client.baidustaging;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/27.
 */
public class BaiduStagingPushResponse {
    private int status; //状态码   0成功；1订单号不存在；2 参数错误
    private String msg; //错误描述      成功：success
    public BaiduStagingPushResponse(){

    }
    public BaiduStagingPushResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaiduStagingPushResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
