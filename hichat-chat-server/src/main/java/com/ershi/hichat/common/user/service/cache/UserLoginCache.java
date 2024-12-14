package com.ershi.hichat.common.user.service.cache;

import com.ershi.hichat.common.common.constant.RedisKey;
import com.ershi.hichat.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 用户登录缓存
 * @author Ershi
 * @date 2024/12/14
 */
@Component
public class UserLoginCache {

    public static final int REMAINING_EXPIRE_TIME_SECONDS = 86400;

    /**
     * 用户token过期时间
     */
    public static final int TOKEN_EXPIRE_DAYS = 3;

    /**
     * 构建token key
     * @param uid
     * @return {@link String}
     */
    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }

    /**
     * 保存token
     * @param token
     * @param uid
     */
    public void saveUserToken(String token, Long uid) {
        String userTokenKey = getUserTokenKey(uid);
        RedisUtils.set(userTokenKey, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    /**
     * 获取token
     * @param uid
     * @return {@link String}
     */
    public String getUserToken(Long uid){
        return RedisUtils.getStr(getUserTokenKey(uid));
    }

    /**
     * 刷新token缓存有效期
     * @param uid
     */
    public void refreshTokenExpireTime(Long uid) {
        String userTokenKey = getUserTokenKey(uid);
        Long expireSeconds = RedisUtils.getExpire(userTokenKey, TimeUnit.SECONDS);
        if (expireSeconds == 0) { // 不存在的key或永久的key => 不需要做续期
            return;
        }
        // 小于必要时间进行刷新
        if(expireSeconds < REMAINING_EXPIRE_TIME_SECONDS) {
            RedisUtils.expire(userTokenKey, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }
}
