package com.ershi.hichat.common.user.dao;

import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.enums.UserStatusEnum;
import com.ershi.hichat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-11-25
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        return lambdaQuery()
                .eq(User::getOpenId, openId)
                .one();
    }

    /**
     * 通过name获取User对象
     *
     * @param name
     * @return {@link User} 若无匹配数据返回null
     */
    public User getByName(String name) {
        return lambdaQuery()
                .eq(User::getName, name)
                .one();
    }

    /**
     * 修改用户名
     *
     * @param uid
     * @param name
     */
    public void modifyName(Long uid, String name) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getName, name)
                .update();
    }

    /**
     * 佩戴徽章
     *
     * @param uid
     * @param badgeId
     */
    public void wearingBadges(Long uid, Long badgeId) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getItemId, badgeId)
                .update();
    }

    /**
     * 更新用户状态为封禁
     *
     * @param uid
     */
    public void invalidUid(Long uid) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getStatus, UserStatusEnum.BAN.getType())
                .update();
    }

    /**
     * 该方法通过查询数据库，获取与给定UID列表匹配的好友信息 <br>
     * 为了保护用户隐私和减少数据传输量，只选择用户ID、活跃状态、头像和名称字段
     *
     * @param friendUidList 好友用户UID列表，用于查询数据库
     * @return {@link List}<{@link User}> 返回一个User对象列表，包含每个好友的选定信息
     */
    public List<User> getFriendList(List<Long> friendUidList) {
        return lambdaQuery()
                .in(User::getId, friendUidList)
                .select(User::getId, User::getActiveStatus, User::getAvatar, User::getName)
                .list();
    }
}
