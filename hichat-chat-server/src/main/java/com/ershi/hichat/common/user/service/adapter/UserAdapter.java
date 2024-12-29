package com.ershi.hichat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ershi.hichat.common.user.domain.entity.ItemConfig;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.UseStatusEnum;
import com.ershi.hichat.common.user.domain.vo.response.BadgeResp;
import com.ershi.hichat.common.user.domain.vo.response.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter {

    /**
     * 构建新增用户对象
     *
     * @param openId
     * @return {@link User}
     */
    public static User buildUserSave(String openId) {
        return User.builder()
                .openId(openId)
                .build();
    }

    /**
     * 获取授权后对用户信息进行补充
     *
     * @param uid
     * @param userInfo
     * @return {@link User}
     */
    public static User buildAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        return User.builder()
                .id(uid)
                .name(userInfo.getNickname())
                .avatar(userInfo.getHeadImgUrl())
                .build();
    }

    /**
     * 构建用户信息
     *
     * @param userInfo         基本用户信息
     * @param modifyNameChance 改名卡次数
     * @return {@link UserInfoResp}
     */
    public static UserInfoResp buildUserInfoResp(User userInfo, Integer modifyNameChance) {
        UserInfoResp userInfoResp = UserInfoResp.builder().build();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(modifyNameChance);
        return userInfoResp;

    }

    /**
     * 构建可选徽章预览
     * @param itemConfigs 所有徽章信息
     * @param backpacks 用户拥有的徽章信息
     * @param user 用户佩戴的徽章
     * @return {@link List}<{@link BadgeResp}>
     */
    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空，防止 NPE 问题
            return Collections.emptyList();
        }

        // 组装已有的徽章状态和已佩戴的徽章状态
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
                    BadgeResp resp = new BadgeResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setObtain(obtainItemSet.contains(a.getId()) ? UseStatusEnum.USED_ALREADY.getStatus() : UseStatusEnum.TO_BE_USED.getStatus());
                    resp.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? UseStatusEnum.USED_ALREADY.getStatus() : UseStatusEnum.TO_BE_USED.getStatus());
                    return resp;
                }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
