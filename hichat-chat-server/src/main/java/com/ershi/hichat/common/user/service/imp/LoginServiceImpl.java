package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.user.service.LoginService;
import org.springframework.stereotype.Service;

/**
 * 登录相关业务处理
 * @author Ershi
 * @date 2024/11/28
 */
@Service
public class LoginServiceImpl implements LoginService {

    /**
     * 用户登录
     * @param uid
     * @return {@link String} 返回token
     */
    @Override
    public String login(Long uid) {
        // todo token补齐
        return null;
    }
}
