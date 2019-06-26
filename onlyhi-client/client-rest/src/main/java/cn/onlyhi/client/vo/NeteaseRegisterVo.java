package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/3.
 */
public class NeteaseRegisterVo {
    private String neteaseAccid;
    private String neteaseToken;

    public String getNeteaseAccid() {
        return neteaseAccid;
    }

    public void setNeteaseAccid(String neteaseAccid) {
        this.neteaseAccid = neteaseAccid;
    }

    public String getNeteaseToken() {
        return neteaseToken;
    }

    public void setNeteaseToken(String neteaseToken) {
        this.neteaseToken = neteaseToken;
    }

    @Override
    public String toString() {
        return "NeteaseRegisterVo{" +
                "neteaseAccid='" + neteaseAccid + '\'' +
                ", neteaseToken='" + neteaseToken + '\'' +
                '}';
    }
}
