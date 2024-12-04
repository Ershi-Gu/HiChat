package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.common.constant.RedisKey;
import com.ershi.hichat.common.common.utils.JwtUtils;
import com.ershi.hichat.common.user.service.LoginService;
import com.ershi.hichat.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关业务处理
 *
 * @author Ershi
 * @date 2024/11/28
 */
@Service
public class LoginServiceImpl implements LoginService {

    public static final int REMAINING_EXPIRE_TIME_SECONDS = 86400;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户token过期时间
     */
    public static final int TOKEN_EXPIRE_DAYS = 3;

    /**
     * 用户登录
     *
     * @param uid
     * @return {@link String} 返回token
     */
    @Override
    public String login(Long uid) {
        // 获取token
        String token = jwtUtils.createToken(uid);
        // 将token保存到Redis中心化管理
        String userTokenKey = getUserTokenKey(uid);
        RedisUtils.set(userTokenKey, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 异步刷新token有效期
     * @param token
     */
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireSeconds = RedisUtils.getExpire(userTokenKey, TimeUnit.SECONDS);
        if (expireSeconds == 0) { // 不存在的key或永久的key => 不需要做续期
            return;
        }
        if(expireSeconds < REMAINING_EXPIRE_TIME_SECONDS) {
            RedisUtils.expire(token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    /**
     * 通过token获取uid
     *
     * @param token
     * @return {@link Long} uid
     */
    @Override
    public Long getValidUid(String token) {
        // 通过token解析uid
        Long uid = jwtUtils.getUidOrNull(token);
        if (uid == null) {
            return null;
        }
        // 验证token是否过期
        String tokenByRedis = RedisUtils.getStr(getUserTokenKey(uid));
        if (StringUtils.isBlank(tokenByRedis)) {
            return null;
        }
        return Objects.equals(token, tokenByRedis) ? uid : null;
    }

    /**
     * 构建token key
     * @param uid
     * @return {@link String}
     */
    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}
