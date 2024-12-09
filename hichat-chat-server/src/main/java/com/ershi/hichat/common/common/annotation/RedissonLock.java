package com.ershi.hichat.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * @author Ershi
 * @date 2024/12/08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLock {

    /**
     * key的前缀,默认取当前方法的全限定名，除非希望在不同方法上对同一个资源做分布式锁，就自己指定
     *
     * @return key的前缀
     */
    String prefixKey() default "";

    /**
     * 锁的主要key值，使用springEl表达式
     *
     * @return 表达式
     */
    String key();

    /**
     * 等待锁的时间，默认-1，不等待
     *
     * @return 单位秒
     */
    int waitTime() default -1;

    /**
     * 等待锁的时间单位，默认毫秒
     *
     * @return 单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
