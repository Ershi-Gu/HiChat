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
import com.ershi.hichat.common.user.domain.vo.request.user.AggregateItemInfoReq;
import com.ershi.hichat.common.user.domain.vo.request.user.AggregateUserInfoReq;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateItemInfoResp;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateUserInfoResp;
import com.ershi.hichat.common.user.domain.vo.response.user.BadgeResp;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;
import com.ershi.hichat.common.user.service.UserService;
import com.ershi.hichat.common.user.service.adapter.AggregateItemInfoAdapter;
import com.ershi.hichat.common.user.service.adapter.BlackAdapter;
import com.ershi.hichat.common.user.service.adapter.UserAdapter;
import com.ershi.hichat.common.user.service.cache.AggregateUserInfoCache;
import com.ershi.hichat.common.user.service.cache.ItemCache;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private UserInfoCache userInfoCache;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private AggregateUserInfoCache aggregateUserInfoCache;

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
        List<UserBackpack> backpacks = userBackpackDao.getByItemsIds(uid,
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

    /**
     * 获取需要刷新的聚合用户信息
     * @param aggregateUserInfoReq
     * @return {@link List }<{@link AggregateUserInfoResp }>
     */
    @Override
    public List<AggregateUserInfoResp> getAggregateUserInfo(AggregateUserInfoReq aggregateUserInfoReq) {
        // 获取需要刷新数据的用户id列表
        List<Long> uidList = getNeedSyncUidList(aggregateUserInfoReq.getReqList());
        // 加载需要刷新的聚合用户信息
        Map<Long, AggregateUserInfoResp> aggregateUserInfoMap = aggregateUserInfoCache.getBatch(uidList);
        // 返回需要刷新的聚合用户信息列表
        return aggregateUserInfoReq.getReqList()
                .stream()
                // 将不需要刷新的用户needRefresh设置为false
                .map(req -> aggregateUserInfoMap.containsKey(req.getUid()) ? aggregateUserInfoMap.get(req.getUid())
                        : AggregateUserInfoResp.skip(req.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取需要刷新的用户id列表
     * @param reqList
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getNeedSyncUidList(List<AggregateUserInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        // 获取缓存中对应uid的用户信息lastModifyTime
        List<Long> userModifyTimeListFromRedis = userInfoCache.getUserLastModifyTime(reqList.stream().map(AggregateUserInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            // 遍历对比前端和后端的lastModifyTime
            AggregateUserInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTimeFromRedis = userModifyTimeListFromRedis.get(i);
            /**
             * 下面情况需要进行懒加载
             * 1. 前端未存储lastModifyTime -> 说明前端没有资源
             * 2. 前端存储的lastModifyTime < 后端的lastModifyTime -> 说明前端资源过期
             */
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTimeFromRedis) && modifyTimeFromRedis > infoReq.getLastModifyTime())) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
    }

    /**
     * 获取聚合徽章信息
     * @param aggregateItemInfoResp
     * @return {@link List }<{@link AggregateItemInfoResp }>
     */
    @Override
    public List<AggregateItemInfoResp> getAggregateItemInfo(AggregateItemInfoReq aggregateItemInfoReq) {
        /**
         * 因为徽章信息不是非常重要的信息，就直接根据db中的更新时间做判断了，没有像用户信息那样缓存判断
         */
        return aggregateItemInfoReq.getReqList().stream().map(reqInfo -> { // 获取请求的徽章id
            // 从缓存中获取徽章信息
            ItemConfig itemConfig = itemCache.getById(reqInfo.getItemId());
            // 根据徽章信息在db中的更新时间判断是否需要刷新前端资源
            if (Objects.nonNull(reqInfo.getLastModifyTime()) && reqInfo.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return AggregateItemInfoResp.skip(reqInfo.getItemId());
            }
            return AggregateItemInfoAdapter.buildResp(itemConfig);
        }).collect(Collectors.toList());
    }
}
