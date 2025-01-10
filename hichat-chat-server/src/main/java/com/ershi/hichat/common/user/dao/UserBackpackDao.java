package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;
import com.ershi.hichat.common.user.domain.enums.UseStatusEnum;
import com.ershi.hichat.common.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @return {@link UserBackpack} 没有物品返回null
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
                .set(UserBackpack::getStatus, UseStatusEnum.USED_ALREADY.getStatus())
                .update();

    }

    /**
     * 查询用户拥有的某类待使用的所有物品
     *
     * @param uid     用户id
     * @param itemIds 物品id列表
     * @return {@link List}<{@link UserBackpack}> 拥有的所有待使用的该物品
     */
    public List<UserBackpack> getByItemsIds(Long uid, List<Long> itemIds) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, UseStatusEnum.TO_BE_USED.getStatus())
                .list();
    }

    /**
     * 查询用户拥有的某类待使用的所有物品
     *
     * @param uid 用户uid列表
     * @param itemIds 物品id列表
     * @return {@link List}<{@link UserBackpack}> 拥有的所有待使用的该物品
     */
    public List<UserBackpack> getByItemsIds(List<Long> uid, List<Long> itemIds) {
        return lambdaQuery()
                .in(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, UseStatusEnum.TO_BE_USED.getStatus())
                .list();
    }

    /**
     * 根据幂等号查询物品是否存在
     * @param idempotentId 幂等号
     * @return {@link UserBackpack} 存在返回对象，反之返回null
     */
    public UserBackpack getByIdp(String idempotentId) {
        return lambdaQuery()
                .eq(UserBackpack::getIdempotent, idempotentId)
                .one();
    }
}
