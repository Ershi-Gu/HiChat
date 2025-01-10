package com.ershi.hichat.common.user.service.cache;

import com.ershi.hichat.common.cache.AbstractRedisStringCache;
import com.ershi.hichat.common.common.constant.RedisKey;
import com.ershi.hichat.common.user.dao.UserBackpackDao;
import com.ershi.hichat.common.user.domain.entity.*;
import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateUserInfoResp;
import com.ershi.hichat.common.user.service.adapter.AggregateUserInfoAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 聚合用户信息缓存 - 实现批量缓存框架[Uid, AggregateUserInfoResp] <br>
 * 该类实际只负责聚合用户基本信息和用户徽章信息，具体的数据从具体的缓存服务类中获取
 * @author Ershi
 * @date 2025/01/10
 */
@Service
public class AggregateUserInfoCache extends AbstractRedisStringCache<Long, AggregateUserInfoResp> {

    @Autowired
    private UserInfoCache userInfoCache;
    
    @Autowired
    private ItemCache itemCache;

    @Autowired
    private UserBackpackDao userBackpackDao;

    /**
     * 聚合用户信息缓存过期时间
     */
    public static final long AGGREGATE_USER_INFO_EXPIRE_SECONDS = 10 * 60L;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.AGGREGATE_USER_INFO_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return AGGREGATE_USER_INFO_EXPIRE_SECONDS;
    }

    /**
     * @param uidList
     * @return {@link Map }<{@link Long }, {@link AggregateUserInfoResp }> 返回结果为Map [Uid, AggregateUserInfoResp]
     */
    @Override
    protected Map<Long, AggregateUserInfoResp> load(List<Long> uidList) {
        // 获取用户基本信息
        Map<Long, User> userInfoMap = userInfoCache.getBatch(uidList);
        // 获取所有徽章信息
        List<ItemConfig> badges = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        List<Long> badgeId = badges.stream().map(ItemConfig::getId).collect(Collectors.toList());
        // 获取用户拥有的徽章信息
        List<UserBackpack> userHasBadgeList = userBackpackDao.getByItemsIds(uidList, badgeId);
        // 将用户背包中的徽章按照uid进行分类存储成map
        Map<Long, List<UserBackpack>> userHasBadgeMap = userHasBadgeList.stream().collect(Collectors.groupingBy(UserBackpack::getUid));

        // 返回结果
        return uidList.stream().map(uid -> {
            // 获取用户信息
            User user = userInfoMap.get(uid);
            if (Objects.isNull(user)) {
                return null;
            }
            // 组装resp并返回
            return AggregateUserInfoAdapter.buildResp(user, userHasBadgeMap);
        }).filter(Objects::nonNull).collect(Collectors.toMap(AggregateUserInfoResp::getUid, Function.identity()));
    }
}
