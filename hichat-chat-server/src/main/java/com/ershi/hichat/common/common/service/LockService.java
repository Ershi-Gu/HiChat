package com.ershi.hichat.common.common.service;


import com.ershi.hichat.common.common.exception.SystemCommonErrorEnum;
import com.ershi.hichat.common.common.utils.AssertUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson分布式锁服务类
 *
 * @author Ershi
 * @date 2024/12/08
 */
@Service
@Slf4j
public class LockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 使用分布式锁执行给定的操作
     *
     * @param key 锁的键
     * @param waitTime 等待锁的时间
     * @param timeUnit 时间单位
     * @param supplier 执行的操作
     * @param <T> 操作返回的类型
     * @return 操作的结果
     * @throws Throwable 如果操作失败或无法获取锁
     */
    public <T> T executeWithLockThrows(String key, int waitTime, TimeUnit timeUnit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean lockSuccess = lock.tryLock(waitTime, timeUnit);
        AssertUtil.isTrue(lockSuccess, SystemCommonErrorEnum.LOCK_LIMIT);
        try {
            return supplier.get();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 使用分布式锁执行给定操作（不抛出异常）
     *
     * @param key
     * @param waitTime
     * @param timeUnit
     * @param supplier
     * @return {@link T}
     */
    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, TimeUnit timeUnit, Supplier<T> supplier) {
        return executeWithLockThrows(key, waitTime, timeUnit, supplier::get);
    }

    /**
     * 使用分布式锁执行给定操作，默认不重试
     * @param key
     * @param supplier
     * @return {@link T}
     */
    public <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }

    @FunctionalInterface
    public interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}
