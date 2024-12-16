package com.ershi.hichat.common.user.service.cache;

import com.ershi.hichat.common.user.dao.BlackDao;
import com.ershi.hichat.common.user.dao.UserRoleDao;
import com.ershi.hichat.common.user.domain.entity.Black;
import com.ershi.hichat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ershi.hichat.common.common.constant.SpringCacheConstant.USER_CACHE_NAME;

/**
 * 用户信息缓存
 *
 * @author Ershi
 * @date 2024/12/15
 */
@Service
// todo 修改为Redis存储框架
public class UserInfoCache {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private BlackDao blackDao;

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

    /**
     * 获取黑名单表
     * @return {@link Map}<{@link Integer}, {@link Set}<{@link String}>> 返回 黑名单类型-[黑名单对象1, 黑名单对象2...]<br>
     * 比如 1-set[uid1, uid2, uid3] 或 2-set[127.0.0.1, 159.68.20.21]
     */
    @Cacheable(cacheNames = USER_CACHE_NAME, key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }
}
