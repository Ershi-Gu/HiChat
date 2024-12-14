package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.common.utils.JwtUtils;
import com.ershi.hichat.common.user.service.LoginService;
import com.ershi.hichat.common.user.service.cache.UserCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 登录相关业务处理
 *
 * @author Ershi
 * @date 2024/11/28
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserCache userCache;


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
        userCache.saveUserToken(token, uid);
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
        userCache.refreshTokenExpireTime(uid);
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
        String tokenByRedis = userCache.getUserToken(uid);
        if (StringUtils.isBlank(tokenByRedis)) {
            return null;
        }
        return Objects.equals(token, tokenByRedis) ? uid : null;
    }

}
