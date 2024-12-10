package com.ershi.hichat.common.user.service.validator;

import com.ershi.hichat.common.user.dao.UserBackpackDao;
import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 徽章发放业务验证器
 * @author Ershi
 * @date 2024/12/08
 */
@Component
@Data
public class BadgeValidator implements ItemValidator {

    /**
     * 当前验证其对应的物品类型
     */
    private static final ItemTypeEnum ITEM_TYPE = ItemTypeEnum.BADGE;

    @Autowired
    private UserBackpackDao userBackpackDao;

    /**
     * 验证期望发放的徽章是否已经存在
     * @param uid 用户id
     * @param itemId 徽章id
     * @return boolean 若徽章已存在则返回false，反之返回true
     */
    @Override
    public boolean validate(Long uid, Long itemId) {
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
        return countByValidItemId <= 0;
    }

    /**
     * 获取当前验证器对应的物品类型
     *
     * @return {@link ItemTypeEnum}
     */
    @Override
    public ItemTypeEnum getItemType() {
        return ITEM_TYPE;
    }
}