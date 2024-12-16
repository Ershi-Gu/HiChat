package com.ershi.hichat.common.user.service.adapter;

import com.ershi.hichat.common.user.domain.entity.Black;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.enums.BlackTypeEnum;
import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * 拉黑表信息转换器
 * @author Ershi
 * @date 2024/12/15
 */
public class BlackAdapter {

    /**
     * 构建拉黑用户信息
     * @param user
     * @return {@link Black}
     */
    public static Black buildBlackUser(User user) {
        return Black.builder()
                .type(BlackTypeEnum.UID.getType())
                .target(user.getId().toString())
                .build();
    }

    /**
     * 构建拉黑ip信息
     *
     * @param ip
     * @return {@link Black}
     */
    public static Black buildBlackIp(String ip) {
        return Black.builder()
                .type(BlackTypeEnum.IP.getType())
                .target(ip)
                .build();
    }
}
