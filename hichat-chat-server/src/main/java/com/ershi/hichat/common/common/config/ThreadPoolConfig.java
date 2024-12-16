package com.ershi.hichat.common.common.config;

import com.ershi.hichat.common.common.factory.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * @author Ershi
 * @date 2024/11/29
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer{

    /**
     * 项目通用线程池
     */
    public static final String HICHAT_EXECUTOR = "hichatExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

    /**
     * 指定@Async使用的线程池
     * @return {@link Executor}
     */
    @Override
    public Executor getAsyncExecutor() {
        return hichatExecutor();
    }

    /**
     * 自定义项目通用线程池
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean(HICHAT_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor hichatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("hichat-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 满了调用线程执行，认为重要任务
        executor.setWaitForTasksToCompleteOnShutdown(true); // 优雅停机 => spring自带，线程池关闭时，等待任务执行完毕再关闭
        executor.setThreadFactory(new MyThreadFactory(executor)); // 自定义线程工厂，内置线程异常处理器
        executor.initialize();
        return executor;
    }

    /**
     * websocket通信共用线程池
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean(WS_EXECUTOR)
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);//支持同时推送1000人
        executor.setThreadNamePrefix("websocket-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());//满了直接丢弃，默认为不重要消息推送
        executor.setThreadFactory(new MyThreadFactory(executor)); // 自定义线程工厂，内置线程异常处理器
        executor.initialize();
        return executor;
    }
}
