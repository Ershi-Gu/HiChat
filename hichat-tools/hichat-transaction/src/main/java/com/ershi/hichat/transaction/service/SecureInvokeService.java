package com.ershi.hichat.transaction.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ershi.hichat.common.utils.JsonUtils;
import com.ershi.hichat.transaction.dao.SecureInvokeRecordDao;
import com.ershi.hichat.transaction.domain.dto.SecureInvokeDTO;
import com.ershi.hichat.transaction.domain.entity.SecureInvokeRecord;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 方法安全调用执行器
 * @author Ershi
 * @date 2025/02/06
 */
@Slf4j
@AllArgsConstructor
public class SecureInvokeService {

    /**
     * 重试间隔时间，至少要入库2分钟
     */
    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private final SecureInvokeRecordDao secureInvokeRecordDao;

    /**
     * 异步执行安全调用所用到的线程池
     */
    private final Executor executor;

    /**
     * 重试方法，每5秒查询一次本地事务表，并进行异步重试
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void retry() {
        List<SecureInvokeRecord> secureInvokeRecords = secureInvokeRecordDao.getWaitRetryRecords();
        for (SecureInvokeRecord secureInvokeRecord : secureInvokeRecords) {
            doAsyncInvoke(secureInvokeRecord);
        }
    }

    public void save(SecureInvokeRecord record) {
        secureInvokeRecordDao.save(record);
    }

    private void retryRecord(SecureInvokeRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord update = new SecureInvokeRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()) {
            update.setStatus(SecureInvokeRecord.STATUS_FAIL);
        } else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDao.updateById(update);
    }

    private Date getNextRetryTime(Integer retryTimes) {//或者可以采用退避算法
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);//重试时间指数上升 2m 4m 8m 16m
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

    private void removeRecord(Long id) {
        secureInvokeRecordDao.removeById(id);
    }

    /**
     * 安全调用方法主体
     * @param record
     * @param async
     */
    public void invoke(SecureInvokeRecord record, boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 判断当前方法是否是事务状态，如果不是则直接执行，不做任何保证。
        if (!inTransaction) {
            return;
        }
        // 保存方法快找到本地事务表 -> 该操作与外部使用@SecureInvoke注解的方法在同一个事务，因此可以保障保存成功
        save(record);
        // 注册一个事务钩子函数，在事务提交后立马执行一次调用，不需要等待定时任务拉取重试
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                // 事务后执行
                if (async) {
                    doAsyncInvoke(record);
                } else {
                    doInvoke(record);
                }
            }
        });
    }

    /**
     * 异步执行方法
     * @param record
     */
    public void doAsyncInvoke(SecureInvokeRecord record) {
        executor.execute(() -> {
//            log.info(Thread.currentThread().getName());
            doInvoke(record);
        });
    }

    /**
     * 同步执行方法
     * @param record
     */
    public void doInvoke(SecureInvokeRecord record) {
        // 获取请求快照参数
        SecureInvokeDTO secureInvokeDTO = record.getSecureInvokeDTO();
        try {
            SecureInvokeHolder.setInvoking();
            Class<?> beanClass = Class.forName(secureInvokeDTO.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings = JsonUtils.toList(secureInvokeDTO.getParameterTypes(), String.class);
            List<Class<?>> parameterClasses = getParameters(parameterStrings);
            Method method = ReflectUtil.getMethod(beanClass, secureInvokeDTO.getMethodName(), parameterClasses.toArray(new Class[]{}));
            Object[] args = getArgs(secureInvokeDTO, parameterClasses);
            // 执行方法
            method.invoke(bean, args);
            // 执行成功更新本地事务表
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("SecureInvokeService invoke fail", e);
            // 执行失败，等待下次重试执行
            retryRecord(record, e.getMessage());
        } finally {
            SecureInvokeHolder.invoked();
        }
    }

    /**
     * 获取方法参数
     *
     * @param secureInvokeDTO
     * @param parameterClasses
     * @return {@link Object[] }
     */
    @NotNull
    private Object[] getArgs(SecureInvokeDTO secureInvokeDTO, List<Class<?>> parameterClasses) {
        JsonNode jsonNode = JsonUtils.toJsonNode(secureInvokeDTO.getArgs());
        Object[] args = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Class<?> aClass = parameterClasses.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return args;
    }

    /**
     *
     * @param parameterStrings
     * @return {@link List }<{@link Class }<{@link ? }>>
     */
    @NotNull
    private List<Class<?>> getParameters(List<String> parameterStrings) {
        return parameterStrings.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("SecureInvokeService class not fund", e);
            }
            return null;
        }).collect(Collectors.toList());
    }
}
