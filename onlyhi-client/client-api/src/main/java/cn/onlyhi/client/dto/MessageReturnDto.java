package cn.onlyhi.client.dto;

/**
 * 短信发送返回信息
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/16.
 */
public class MessageReturnDto extends BaseDto {

    private MessageReturnDto(){

    }

    public MessageReturnDto(int state, String stateValue) {
        this.state = state;
        this.stateValue = stateValue;
    }

    private int state;
    private String stateValue;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }
}
