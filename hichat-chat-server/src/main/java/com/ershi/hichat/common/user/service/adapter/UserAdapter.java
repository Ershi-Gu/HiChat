package com.ershi.hichat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

public class UserAdapter {

    /**
     * 构建新增用户对象
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
     * @param userInfo 基本用户信息
     * @param modifyNameChance 改名卡次数
     * @return {@link UserInfoResp}
     */
    public static UserInfoResp buildUserInfoResp(User userInfo, Integer modifyNameChance) {
        UserInfoResp userInfoResp = UserInfoResp.builder().build();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(modifyNameChance);
        return userInfoResp;

    }
}
