package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.vo.request.user.AggregateItemInfoReq;
import com.ershi.hichat.common.user.domain.vo.request.user.AggregateUserInfoReq;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateItemInfoResp;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateUserInfoResp;
import com.ershi.hichat.common.user.domain.vo.response.user.BadgeResp;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-11-25
 */
public interface UserService {


    void register(User insert);


    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearingBadges(Long badgeId);

    void blackUser(Long uid);

    void blackUserAndIp(Long uid);

    List<AggregateUserInfoResp> getAggregateUserInfo(AggregateUserInfoReq aggregateUserInfoReq);

    List<AggregateItemInfoResp> getAggregateItemInfo(AggregateItemInfoReq aggregateItemInfoReq);
}
