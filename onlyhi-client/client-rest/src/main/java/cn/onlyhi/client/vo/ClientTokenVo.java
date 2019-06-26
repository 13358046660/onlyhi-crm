package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/7.
 */
public class ClientTokenVo {
    private String clientToken;

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    @Override
    public String toString() {
        return "ClientTokenVo{" +
                "clientToken='" + clientToken + '\'' +
                '}';
    }
}
