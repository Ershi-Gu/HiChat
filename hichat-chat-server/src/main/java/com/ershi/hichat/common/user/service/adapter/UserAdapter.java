package com.ershi.hichat.common.user.service.adapter;

import com.ershi.hichat.common.user.domain.entity.User;
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
}
