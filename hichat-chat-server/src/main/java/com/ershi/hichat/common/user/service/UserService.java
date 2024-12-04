package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-11-25
 */
public interface UserService {


    Long register(User insert);


    UserInfoResp getUserInfo(Long uid);
}
