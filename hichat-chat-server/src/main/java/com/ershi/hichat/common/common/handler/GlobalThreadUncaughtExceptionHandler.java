package com.ershi.hichat.common.common.handler;

import lombok.extern.slf4j.Slf4j;


/**
 * 全局线程异常处理器
 * @author Ershi
 * @date 2024/11/30
 */
@Slf4j
public class GlobalThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final GlobalThreadUncaughtExceptionHandler INSTANCE = new GlobalThreadUncaughtExceptionHandler();

    /**
     * 线程异常处理
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread {}", t.getName(), e);
    }

    /**
     * 获取单例
     * @return {@link GlobalThreadUncaughtExceptionHandler}
     */
    public static GlobalThreadUncaughtExceptionHandler getInstance() {
         return INSTANCE;
     }
}
