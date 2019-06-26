package cn.onlyhi.client.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/2.
 */
@Component
public class Producter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Producter.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String msg) {
        LOGGER.info("发送消息: {}", msg);
        send(MQConstant.QUENE_NAME, msg, 1000 * 60 * 60 * 2);   //二小时
    }

    private void send(String queueName, String msg, long times) {
        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setConsumerQueue(queueName);
                message.getMessageProperties().setExpiration(times + "");
                return message;
            }
        };
        rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_NAME, MQConstant.DELAY_NAME, msg, processor);
        LOGGER.info("发送消息成功: {}，{}，{}", queueName, msg, times);
    }
}
