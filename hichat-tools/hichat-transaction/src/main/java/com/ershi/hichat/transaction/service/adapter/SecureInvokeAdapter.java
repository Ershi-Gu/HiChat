package com.ershi.hichat.transaction.service.adapter;

import cn.hutool.core.date.DateUtil;
import com.ershi.hichat.common.utils.JsonUtils;
import com.ershi.hichat.transaction.annotation.SecureInvoke;
import com.ershi.hichat.transaction.domain.dto.SecureInvokeDTO;
import com.ershi.hichat.transaction.domain.entity.SecureInvokeRecord;
import com.ershi.hichat.transaction.service.SecureInvokeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 安全调用快照信息处理器
 * @author Ershi
 * @date 2025/02/06
 */
public class SecureInvokeAdapter {

    /**
     * 构建安全调用方法快照信息
     * @param joinPoint
     * @param secureInvoke
     * @return {@link SecureInvokeRecord }
     */
    public static SecureInvokeRecord buildSecureInvokeRecord(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        SecureInvokeDTO dto = SecureInvokeDTO.builder()
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameters))
                .build();
        return SecureInvokeRecord.builder()
                .secureInvokeDTO(dto)
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .build();
    }
}
