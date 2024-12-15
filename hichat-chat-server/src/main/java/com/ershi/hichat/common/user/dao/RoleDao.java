package com.ershi.hichat.common.user.dao;

import com.ershi.hichat.common.user.domain.entity.Role;
import com.ershi.hichat.common.user.mapper.RoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-14
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
