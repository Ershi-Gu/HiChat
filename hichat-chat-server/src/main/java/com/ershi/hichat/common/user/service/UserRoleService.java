package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.user.domain.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ershi.hichat.common.user.domain.enums.RoleEnum;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-14
 */
public interface UserRoleService {

    /**
     * 检查当前用户权限是否符合需求权限
     *
     * @param uid
     * @param roleEnum
     * @return boolean
     */
    boolean checkAuth(Long uid, RoleEnum roleEnum);

    /**
     * 判断是否是超管，超管拥有所有权限
     * @param roleSet
     * @return boolean
     */
    boolean isAdmin(Set<Long> roleSet);

    /**
     * 获取用户最高身份权限id
     * @param uid
     * @return {@link Long} 用户无特殊身份返回 0
     */
    Integer getUserTopRule(Long uid);
}
