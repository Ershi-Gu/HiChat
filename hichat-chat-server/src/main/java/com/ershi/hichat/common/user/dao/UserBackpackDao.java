package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.UseStatusEnum;
import com.ershi.hichat.common.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-03
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    /**
     * 返回目标用户背包中的可用目标物品数
     *
     * @param uid    用户id
     * @param itemId 物品id
     * @return {@link Integer} 可用物品数
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getId, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, UseStatusEnum.TO_BE_USED.getStatus())
                .count();
    }

    /**
     * 获取目标用户背包中的最早获得的可用目标物品
     *
     * @param uid    用户id
     * @param itemId 物品id
     * @return boolean 是否获取到
     */
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, UseStatusEnum.TO_BE_USED.getStatus())
                .orderByAsc(UserBackpack::getCreateTime)
                .last("limit 1")
                .one();
    }

    /**
     * 使用物品（通过行级锁实现乐观锁）
     *
     * @param item 物品
     * @return boolean 使用是否成功
     */
    public boolean useItem(UserBackpack item) {
        return lambdaUpdate()
                .eq(UserBackpack::getId, item.getId())
                .eq(UserBackpack::getStatus, item.getStatus())
                .set(UserBackpack::getStatus, UseStatusEnum.USED_ALREADY)
                .update();

    }
}
