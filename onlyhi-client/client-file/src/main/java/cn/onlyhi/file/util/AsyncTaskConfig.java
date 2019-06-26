package cn.onlyhi.file.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author WQZ
 * <p>
 * Created by WQZ on 2018/11/07.
 */
@Configuration
@ComponentScan(basePackages = { "cn.onlyhi.file.util" })
// 利用@EnableAsync注解开启异步任务支持
//@EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        // 配置类实现AsyncConfigurer接口并重写 getAsyncExecutor 方法,
        // 并返回一个 ThreadPoolTaskExecutor,这样我们就获得了一个线程池 taskExecutor
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(80);
        //最大线程数
        taskExecutor.setMaxPoolSize(92);
        //队列最大长度
        taskExecutor.setQueueCapacity(80);
        //线程池维护线程所允许的空闲时间
        taskExecutor.setKeepAliveSeconds(300);
        //线程池对拒绝任务(无线程可用)的处理策略 ThreadPoolExecutor.CallerRunsPolicy策略
        //调用者的线程会执行该任务,如果执行器已关闭,则丢弃
        //当有新任务添加到线程池被拒绝时，线程池会将被拒绝的任务添加到"线程池正在运行的线程"中去运行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}

