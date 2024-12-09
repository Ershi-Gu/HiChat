package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.user.domain.enums.IdempotentSourceEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-03
 */
public interface UserBackpackService {

    /**
     * 用户获取物品
     * @param uid 用户uid
     * @param itemId 获取物品id
     * @param idempotentSourceEnum 幂等号来源渠道类型
     * @param businessId 该渠道下的业务号，用于组装幂等号
     */
    void acquireItem(Long uid, Long itemId, IdempotentSourceEnum idempotentSourceEnum, String businessId) throws Throwable;

    /**
     * 执行发放物品
     *
     * @param uid          用户id
     * @param itemId       物品id
     * @param idempotentId 幂等号
     */
    void doAcquireItem(Long uid, Long itemId, String idempotentId);
}
