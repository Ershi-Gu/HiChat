package com.ershi.hichat.common.user.dao;

import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.UseStatusEnum;
import com.ershi.hichat.common.user.mapper.UserBackpackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
     * @param uid 用户id
     * @param itemId 物品id
     * @return {@link Integer} 可用物品数
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery().eq(UserBackpack::getId, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, UseStatusEnum.TO_BE_USED.getStatus())
                .count();
    }
}
