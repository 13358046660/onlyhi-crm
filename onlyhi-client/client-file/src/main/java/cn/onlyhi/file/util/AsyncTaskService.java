package cn.onlyhi.file.util;
import cn.onlyhi.file.request.SyncParamRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static cn.onlyhi.common.util.CmdExecuteUtil.exec;

/**
 * 任务执行类
 *
 * @Author WQZ
 * <p>
 * Created by WQZ on 2018/11/07.
 */
@Service
public class AsyncTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTaskService.class);

    // 通过@Async注解表明该方法是一个异步方法，如果注解在类级别，表明该类下所有方法都是异步方法，而这里的方法自动被注入使用ThreadPoolTaskExecutor 作为 TaskExecutor
    @Async
    public void executeAsyncTask(String[] cmds, Integer curPersonNum) {
        LOGGER.info("curPersonNum={}",curPersonNum);
        try {
            for (int i = 0; i < curPersonNum; i++) {
                if (cmds.length > 0) {
                    exec(cmds, false);
                    LOGGER.info("第{}次开启录制",i);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
 /*   @Async
    public void executeAsyncTaskPlus(Integer i){
        System.out.println("执行异步任务+1：" + (i+1));
    }*/
 /*   public static void main(String[] args){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncTaskConfig.class);

        AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);

        for(int i = 0; i < 10; i++){
            asyncTaskService.executeAsyncTask(null,i);
            asyncTaskService.executeAsyncTaskPlus(i);
        }
    }*/
}

