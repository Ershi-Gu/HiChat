package com.ershi.hichat.common.user.dao;

import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.mapper.UserMapper;
import com.ershi.hichat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-11-25
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User>{

}
