package cn.onlyhi.client.rabbitmq;

/**
 * 消息队列相关常量
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/2.
 */
public class MQConstant {
    private MQConstant(){
    }

    public static final String EXCHANGE_NAME = "order.delay.exchange";

    public static final String ROUTING_KEY = "order.delay.routing";

    public static final String DELAY_NAME = "order.delay";

    public static final String QUENE_NAME = "order.quene";
}
