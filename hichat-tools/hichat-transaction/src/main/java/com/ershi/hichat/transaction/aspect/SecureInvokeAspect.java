package com.ershi.hichat.transaction.aspect;

import cn.hutool.core.date.DateUtil;
import com.ershi.hichat.common.utils.JsonUtils;
import com.ershi.hichat.transaction.annotation.SecureInvoke;
import com.ershi.hichat.transaction.domain.dto.SecureInvokeDTO;
import com.ershi.hichat.transaction.domain.entity.SecureInvokeRecord;
import com.ershi.hichat.transaction.service.SecureInvokeHolder;
import com.ershi.hichat.transaction.service.SecureInvokeService;
import com.ershi.hichat.transaction.service.adapter.SecureInvokeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法安全调用切面
 * @author Ershi
 * @date 2025/02/06
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1)//确保最先执行
@Component
public class SecureInvokeAspect {
    @Autowired
    private SecureInvokeService secureInvokeService;

    @Around("@annotation(secureInvoke)")
    public Object around(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) throws Throwable {
        // 判断是否是需要异步执行
        boolean async = secureInvoke.async();
        // 获取方法是否在事务内
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 非事务状态，直接执行，不做任何保证 => 不是事务方法不需要启用本地事务去保证安全
        if (SecureInvokeHolder.isInvoking() || !inTransaction) {
            return joinPoint.proceed();
        }
        // 获取方法的快照参数，用于保存到本地事务表
        SecureInvokeRecord record = SecureInvokeAdapter.buildSecureInvokeRecord(joinPoint, secureInvoke);
        // 安全调用方法
        secureInvokeService.invoke(record, async);
        return null;
    }
}
