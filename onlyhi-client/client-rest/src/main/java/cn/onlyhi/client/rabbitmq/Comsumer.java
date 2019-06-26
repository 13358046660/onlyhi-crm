package cn.onlyhi.client.rabbitmq;

import cn.onlyhi.client.po.CpPayRequest;
import cn.onlyhi.client.service.CpPayRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/2.
 */
@Component
@RabbitListener(queues = MQConstant.ROUTING_KEY)
public class Comsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Comsumer.class);

    @Autowired
    private CpPayRequestService cpPayRequestService;

    @RabbitHandler
    public void process(String orderUuid) {
        LOGGER.info("process::orderUuid={}", orderUuid);
        CpPayRequest cpPayRequest = new CpPayRequest();
        cpPayRequest.setUuid(orderUuid);
        cpPayRequest.setStatus(false);
        int i = cpPayRequestService.update(cpPayRequest);
        if (i > 0) {
            LOGGER.info("订单 {} 关闭成功！", orderUuid);
        }
    }
}
