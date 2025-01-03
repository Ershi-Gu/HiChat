package com.ershi.hichat.common.user.service.imp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ershi.hichat.common.common.event.UserBlackEvent;
import com.ershi.hichat.common.common.event.UserRegisterEvent;
import com.ershi.hichat.common.common.exception.BusinessException;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.user.dao.BlackDao;
import com.ershi.hichat.common.user.dao.UserBackpackDao;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.Black;
import com.ershi.hichat.common.user.domain.entity.ItemConfig;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.ItemEnum;
import com.ershi.hichat.common.user.domain.enums.ItemTypeEnum;
import com.ershi.hichat.common.user.domain.vo.response.user.BadgeResp;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;
import com.ershi.hichat.common.user.service.UserService;
import com.ershi.hichat.common.user.service.adapter.BlackAdapter;
import com.ershi.hichat.common.user.service.adapter.UserAdapter;
import com.ershi.hichat.common.user.service.cache.ItemCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BlackDao blackDao;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 注册
     *
     * @param insert
     * @return {@link Long}
     */
    @Override
    public void register(User insert) {
        // 注册用户到数据库
        boolean save = userDao.save(insert);
        // 发送用户注册的事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
    }

    /**
     * 获取当前登录用户信息
     *
     * @param uid
     * @return {@link UserInfoResp}
     */
    @Override
    public UserInfoResp getUserInfo(Long uid) {
        // 获取用户基本信息
        User userInfo = userDao.getById(uid);
        // 获取剩余改名次数
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        // 构建用户信息返回
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

    /**
     * 修改用户名
     *
     * @param uid
     * @param name
     */
    @Override
    @Transactional
    public void modifyName(Long uid, String name) {
        // 判断名字是否重复
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "名字已存在，换个名字吧~");
        // 判断改名卡是否够用
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不够咯，暂时无法改名啦~");
        // 使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) { // 通过数据库的行级锁实现乐观锁
            // 改名
            userDao.modifyName(uid, name);
        }
    }

    /**
     * 可选徽章预览，优先展示已佩戴的和已拥有的
     *
     * @param uid 用户id
     * @return {@link List}<{@link BadgeResp}> 所有徽章列表，包括可选和不可选的
     */
    @Override
    public List<BadgeResp> badges(Long uid) {
        // 查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        if (CollUtil.isEmpty(itemConfigs)) { // 如果系统暂无徽章，返回空
            return Collections.emptyList();
        }
        // 查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemsId(uid,
                itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //查询用户当前佩戴的标签
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    /**
     * 为当前用户佩戴目标徽章
     * @param badgeId 徽章id
     */
    @Override
    public void wearingBadges(Long badgeId) {
        // 确保佩戴物品存在用户背包
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(RequestHolder.get().getUid(), badgeId);
        AssertUtil.isNotEmpty(firstValidItem, "你还没有该物品哦，快去查看怎么获得吧~");
        // 确保该物品类型是徽章
        ItemConfig item = itemCache.getById(firstValidItem.getItemId());
        AssertUtil.equal(item.getType(), ItemTypeEnum.BADGE.getType(), "该物品不可佩戴，请检查佩戴徽章是否正确！");
        // 佩戴徽章
        userDao.wearingBadges(RequestHolder.get().getUid(), badgeId);
    }

    /**
     * 拉黑用户
     * @param uid
     */
    @Override
    public void blackUser(Long uid) {
        // 获取用户信息
        User user = userDao.getById(uid);
        AssertUtil.nonNull(user, "封禁目标用户不存在，请重新检查！");
        // 构建black表需要的信息
        Black blackUser = BlackAdapter.buildBlackUser(user);
        // 更新black表
        try {
            blackDao.save(blackUser);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("重复封禁用户");
        }
        // 发出封禁事件
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, user));

    }

    /**
     * 封禁用户及ip
     * @param uid
     */
    @Override
    @Transactional
    public void blackUserAndIp(Long uid) {
        // 封禁用户
        blackUser(uid);
        // 获取用户ip
        User user = userDao.getById(uid);
        String updateIp = user.getIpInfo().getUpdateIp();
        // 封禁用户最近登录ip
        blackIp(updateIp);
    }

    /**
     * 封禁ip
     * @param ip
     */
    private void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        // 构建black对象
        Black blackIp = BlackAdapter.buildBlackIp(ip);
        try {
            blackDao.save(blackIp);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("重复封禁ip");
        }
    }
}
