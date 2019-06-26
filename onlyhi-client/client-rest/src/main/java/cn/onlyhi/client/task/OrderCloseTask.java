package cn.onlyhi.client.task;


import cn.onlyhi.client.service.CpPayRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单关闭定时任务（防止rabbitmq出问题后订单未处理情况）
 * 关闭前一天未关闭订单
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/5/23.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class OrderCloseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCloseTask.class);

    @Autowired
    private CpPayRequestService cpPayRequestService;

    @Scheduled(cron = "0 30 2 * * ? ")    //每天凌晨2:30点执行
    public void orderClose() throws Exception {
        LOGGER.info("订单关闭定时任务开始！");
        int i = cpPayRequestService.closeNoPayOrder();
        if (i > 0) {
            LOGGER.info("订单关闭定时任务结束！");
        } else if (i == 0) {
            LOGGER.info("订单关闭定时任务结束！没有要关闭的订单！");
        } else {
            LOGGER.info("订单关闭定时任务异常！");
        }
    }

}
