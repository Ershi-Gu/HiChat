package com.ershi.hichat.common.user.service.adapter;

import com.ershi.hichat.common.user.domain.entity.IpDetail;
import com.ershi.hichat.common.user.domain.entity.IpInfo;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateUserInfoResp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AggregateUserInfoAdapter {

    /**
     * 构建聚合用户信息返回
     * @param user 用户信息
     * @param userHasBadgeMap 用户拥有的徽章map
     * @return {@link AggregateUserInfoResp }
     */
    public static AggregateUserInfoResp buildResp(User user, Map<Long, List<UserBackpack>> userHasBadgeMap) {
        AggregateUserInfoResp aggregateUserInfoResp = new AggregateUserInfoResp();
        List<UserBackpack> userBackpacks = userHasBadgeMap.getOrDefault(user.getId(), new ArrayList<>());
        aggregateUserInfoResp.setUid(user.getId());
        aggregateUserInfoResp.setName(user.getName());
        aggregateUserInfoResp.setAvatar(user.getAvatar());
        aggregateUserInfoResp.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
        aggregateUserInfoResp.setWearingItemId(user.getItemId());
        aggregateUserInfoResp.setItemIds(userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
        return aggregateUserInfoResp;
    }
}
