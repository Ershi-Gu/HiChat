package com.ershi.hichat.common.user.service.validator;

import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;

/**
 * 物品发放业务校验接口，用于物品发放正确性相关校验
 *
 * @author Ershi
 * @date 2024/12/08
 */
public interface ItemValidator {

    /**
     * 校验物品是否合法
     *
     * @param uid    用户ID
     * @param itemId 物品ID
     */
    boolean validate(Long uid, Long itemId);

    /**
     * 获取当前验证器对应的物品类型
     *
     * @return {@link ItemTypeEnum}
     */
    ItemTypeEnum getItemType();
}
