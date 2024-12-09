package com.ershi.hichat.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.ershi.hichat.common.common.annotation.RedissonLock;
import com.ershi.hichat.common.common.service.LockService;
import com.ershi.hichat.utils.SpElUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redisson分布式锁切面类
 *
 * @author Ershi
 * @date 2024/12/08
 */
@Aspect
@Component
@Order(0) // 分布式锁要在事务注解前执行
public class RedissonLockAspect {

    @Autowired
    private LockService lockService;

    /**
     * 为打上@RedissonLock注解的方法启用Redisson分布式锁，并设置key
     *
     * @param joinPoint
     * @return {@link Object}
     */
    @Around("@annotation(com.ershi.hichat.common.common.annotation.RedissonLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取被拦截方法的方法对象
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 获取方法上的RedissonLock注解
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        // 确定锁的前缀key：如果注解的prefixKey属性为空，则使用SpEl表达式获取“类名#方法名”；否则使用prefixKey属性值
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();//默认方法限定名+注解排名（可能多个）
        // 解析SpEl表达式，获取锁的键值
        String key = SpElUtils.parseMethodArgsSpEl(method, joinPoint.getArgs(), redissonLock.key());
        // 执行拦截方法，加分布式锁
        return lockService.executeWithLockThrows(prefix + ":" + key, redissonLock.waitTime(), redissonLock.timeUnit(), joinPoint::proceed);
    }
}
