package com.ershi.hichat.common.user.service.cache;

import com.ershi.hichat.common.user.dao.UserRoleDao;
import com.ershi.hichat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ershi.hichat.common.common.constant.SpringCacheConstant.USER_CACHE_NAME;

/**
 * 用户信息缓存
 * @author Ershi
 * @date 2024/12/15
 */
@Service
// todo 修改为Redis存储框架
public class UserInfoCache {

    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 获取用户身份id集合set
     * @param uid
     * @return {@link Set}<{@link Long}> 查询不到返回空集合[]
     */
    @Cacheable(cacheNames = USER_CACHE_NAME, key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
