package cn.onlyhi.client.baidustaging;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/27.
 */
public class BaiduStagingResponse {
    private int status; //0:成功
    private String msg; //成功：success
    private String result;  //成功后 url PC扫码页面或者订单确认页面

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "BaiduStagingResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
