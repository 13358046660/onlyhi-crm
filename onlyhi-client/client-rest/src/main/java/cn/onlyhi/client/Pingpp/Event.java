package cn.onlyhi.client.Pingpp;

import cn.onlyhi.common.request.BaseRequest;
import com.pingplusplus.model.Charge;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/25.
 */
public class Event extends BaseRequest{
    private String id;
    private Long created;
    private boolean livemode;
    private String type;
    private String object;
    private String request;
    private boolean pending_webhooks;
    private ChargeSucceededData data;
    public Event(){

    }
    public class ChargeSucceededData{
        public ChargeSucceededData(){

        }
        private Charge object;

        public Charge getObject() {
            return object;
        }

        public void setObject(Charge object) {
            this.object = object;
        }

        @Override
        public String toString() {
            return "ChargeSucceededData{" +
                    "object=" + object +
                    '}';
        }
        /*private ChargeSucceededObject object;
        class ChargeSucceededObject{
            private String id;
            private String object;
            private Long created;
            private boolean livemode;
            private boolean paid;
            private boolean refunded;
            private String app;
            private String channel;
            private String order_no;
            private String client_ip;
            private int amount;
            private int amount_settle;
            private String currency;
            private String subject;
            private String body;
            private Long time_paid;
            private Long time_expire;
            private Long time_settle;
            private String transaction_no;
            private int amount_refunded;
            private String failure_code;
            private String failure_msg;
            private String description;

        }*/
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public boolean isLivemode() {
        return livemode;
    }

    public void setLivemode(boolean livemode) {
        this.livemode = livemode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public boolean isPending_webhooks() {
        return pending_webhooks;
    }

    public void setPending_webhooks(boolean pending_webhooks) {
        this.pending_webhooks = pending_webhooks;
    }

    public ChargeSucceededData getData() {
        return data;
    }

    public void setData(ChargeSucceededData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", livemode=" + livemode +
                ", type='" + type + '\'' +
                ", object='" + object + '\'' +
                ", request='" + request + '\'' +
                ", pending_webhooks=" + pending_webhooks +
                ", data=" + data +
                '}';
    }
}
