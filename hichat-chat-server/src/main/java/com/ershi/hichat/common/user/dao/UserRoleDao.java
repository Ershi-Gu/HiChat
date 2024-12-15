package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.user.domain.entity.UserRole;
import com.ershi.hichat.common.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-14
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {

    /**
     * 用户用户身份列表
     * @param uid
     * @return {@link List}<{@link UserRole}>
     */
    public List<UserRole> listByUid(Long uid) {
        return lambdaQuery()
                .eq(UserRole::getUid, Objects.requireNonNull(uid))
                .list();
    }
}
