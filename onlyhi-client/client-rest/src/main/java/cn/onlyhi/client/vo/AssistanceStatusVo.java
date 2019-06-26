package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/8.
 */
public class AssistanceStatusVo {
    private int assistanceStatus;

    public int getAssistanceStatus() {
        return assistanceStatus;
    }

    public void setAssistanceStatus(int assistanceStatus) {
        this.assistanceStatus = assistanceStatus;
    }

    @Override
    public String toString() {
        return "AssistanceStatusVo{" +
                "assistanceStatus=" + assistanceStatus +
                '}';
    }
}
