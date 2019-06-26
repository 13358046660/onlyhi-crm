package cn.onlyhi.common.enums;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/13.
 */
public class MessageSendTemplateEnum {

    /**
     * 短信发送用途
     */
    public enum PURPOSE {

        APP(1, "app端发送");

        public int key;
        public String value;

        PURPOSE(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
