package cn.onlyhi.file.task;
import cn.onlyhi.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 恢复之前因为异常没存上的进出房间记录
 * @Author wqz
 * <p>
 * Created by wqz on 2018/8/1.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class RestoreRecordTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestoreRecordTask.class);
    //非每天凌晨2:10点执行
    //@Scheduled(cron = "0 10 2 * * ? ")
    public void exportDataTask() {
        LOGGER.info("restoreRecord...start...");
        String url = "http://localhost:20027/client/record/restoreRecord?restoreType=4";
        LOGGER.info("url={}", url);
        String result = HttpUtil.sendGet(url);
        LOGGER.info("result={}", result);
        LOGGER.info("restoreRecord...end...");
    }
}
