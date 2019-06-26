package cn.onlyhi.client.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建RabbitMQ的配置类RabbitConfig，用来配置队列、交换器、路由等高级信息。
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/1.
 */
@Configuration
public class RabbitConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConfig.class);

    @Bean
    public DirectExchange defaultExchange() {
        LOGGER.info("defaultExchange::");
        return new DirectExchange(MQConstant.EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue repeatTradeQueue() {
        Queue queue = new Queue(MQConstant.ROUTING_KEY, true, false, false);
        LOGGER.info("repeatTradeQueue::" + queue.getArguments());
        return queue;
    }

    @Bean
    public Binding drepeatTradeBinding() {
        LOGGER.info("drepeatTradeBinding::");
        return BindingBuilder.bind(repeatTradeQueue()).to(defaultExchange()).with(MQConstant.ROUTING_KEY);
    }

    @Bean
    public Queue deadLetterQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstant.EXCHANGE_NAME);
        arguments.put("x-dead-letter-routing-key", MQConstant.ROUTING_KEY);
        Queue queue = new Queue(MQConstant.DELAY_NAME, true, false, false, arguments);
        LOGGER.info("deadLetterQueue::" + queue.getArguments());
        return queue;
    }

    @Bean
    public Binding deadLetterBinding() {
        LOGGER.info("deadLetterBinding::");
        return BindingBuilder.bind(deadLetterQueue()).to(defaultExchange()).with(MQConstant.DELAY_NAME);
    }


    /*********************
     * 订单order 队列
     *****************/
    @Bean
    public Queue queue() {
        Queue queue = new Queue(MQConstant.QUENE_NAME, true);
        LOGGER.info("queue::" + queue.getArguments());
        return queue;
    }

    @Bean
    public Binding binding() {
        LOGGER.info("binding::");
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(MQConstant.QUENE_NAME);
    }
}
