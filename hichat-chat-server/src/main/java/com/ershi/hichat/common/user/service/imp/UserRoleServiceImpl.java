package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.user.domain.enums.RoleEnum;
import com.ershi.hichat.common.user.service.UserRoleService;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserInfoCache userInfoCache;

    /**
     * 检查用户身份是否满足要求
     * @param uid 用户id
     * @param requireRoleEnum 要求身份
     * @return boolean
     */
    @Override
    public boolean checkAuth(Long uid, RoleEnum requireRoleEnum) {
        Set<Long> roleSet = userInfoCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(requireRoleEnum.getId());
    }

    /**
     * 判断是否是超管，超管拥有所有权限
     * @param roleSet
     * @return boolean
     */
    public boolean isAdmin(Set<Long> roleSet) {
        return Objects.requireNonNull(roleSet).contains(RoleEnum.ADMIN.getId());
    }

    /**
     * 获取用户最高身份权限id
     *
     * @param uid
     * @return {@link Integer} 用户无特殊身份返回 0
     */
    public Integer getUserTopRule(Long uid) {
        Set<Long> roleSet = userInfoCache.getRoleSet(uid);
        if (roleSet.isEmpty()) {
            return 0;
        }
        return Math.toIntExact(Collections.min(roleSet));
    }
}
