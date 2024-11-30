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
        executor.setThreadFactory(new MyThreadFactory(executor)); // 创建自定义线程工厂，以该spring线程工厂作为基础工厂
        executor.initialize();
        return executor;
    }
}
