package com.ershi.hichat.common.user.service.cache;

import com.ershi.hichat.common.cache.AbstractRedisStringCache;
import com.ershi.hichat.common.common.constant.RedisKey;
import com.ershi.hichat.common.user.dao.BlackDao;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.dao.UserRoleDao;
import com.ershi.hichat.common.user.domain.entity.Black;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserRole;
import com.ershi.hichat.common.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ershi.hichat.common.common.constant.SpringCacheConstant.USER_CACHE_NAME;

/**
 * 用户信息缓存 - 实现批量缓存框架[Uid, User]
 *
 * @author Ershi
 * @date 2024/12/15
 */
@Service
public class UserInfoCache extends AbstractRedisStringCache<Long, User> {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private BlackDao blackDao;

    @Autowired
    private UserDao userDao;

    /**
     * 用户信息缓存过期时间
     */
    public static final long USER_INFO_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return USER_INFO_EXPIRE_SECONDS;
    }

    /**
     * @param uidList
     * @return {@link Map }<{@link Long }, {@link User }> map[uid-User]
     */
    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        // 从数据库加载用户信息
        List<User> loadUserInfoList = userDao.listByIds(uidList);
        return loadUserInfoList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }

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

    /**
     * 批量从缓存获取用户信息最后一次更新时间
     *
     * @param uidList
     * @return {@link List }<{@link Long }>
     */
    public List<Long> getUserLastModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_LAST_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }

    /**
     * 判断用户是否在线
     * @param uid
     * @return boolean
     */
    public boolean isOnline(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zIsMember(onlineKey, uid);
    }
}
