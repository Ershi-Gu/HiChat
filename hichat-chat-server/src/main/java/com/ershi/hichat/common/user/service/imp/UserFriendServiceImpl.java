package com.ershi.hichat.common.user.service.imp;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ershi.hichat.common.common.annotation.RedissonLock;
import com.ershi.hichat.common.common.event.UserApplyEvent;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.request.PageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.domain.vo.response.PageBaseResp;
import com.ershi.hichat.common.user.dao.UserApplyDao;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.dao.UserFriendDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserApply;
import com.ershi.hichat.common.user.domain.entity.UserFriend;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApplyReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApproveReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendCheckReq;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendApplyResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendCheckResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendUnreadResp;
import com.ershi.hichat.common.user.service.UserFriendService;
import com.ershi.hichat.common.user.service.adapter.FriendAdapter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ershi.hichat.common.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;

/**
 * 联系人服务模块
 * @author Ershi
 * @date 2024/12/30
 */
@Service
@Slf4j
public class UserFriendServiceImpl implements UserFriendService {

    @Autowired
    private UserFriendDao userFriendDao;

    @Autowired
    private UserApplyDao userApplyDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 批量检查目标对象是否是自己好友
     * @param uid 当前登录用户 id
     * @param friendCheckReq 包含目标用户uid列表
     * @return {@link FriendCheckResp}
     */
    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq friendCheckReq) {
        // 查询目标列表中是否包含好友
        List<UserFriend> friendList = userFriendDao.getFriendsInTargetList(uid, friendCheckReq.getUidList());
        // 构建好友验证返回
        return FriendAdapter.buildFriendCheckResp(friendList, friendCheckReq);
    }


    /**
     * 获取好友列表的分页返回
     *
     * @param uid 当前用户uid
     * @param cursorPageBaseReq 分页请求信息
     * @return {@link CursorPageBaseResp}<{@link FriendResp}> 返回好友uid列表，具体好友信息由前端懒加载
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        // 获取当前用户的好友表数据
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, cursorPageBaseReq);
        // 判断是否为空
        if (friendPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        // 根据好友表数据获取所有好友的uid
        List<Long> friendUidList = friendPage.getList()
                .stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        // 根据uid获取好友详细用户信息
        List<User> friendUserList = userDao.getFriendList(friendUidList);
        // 将数据转换成响应格式返回
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriendRespList(friendUserList));
    }

    /**
     * 申请好友功能
     * @param uid
     * @param friendApplyReq
     */
    @Override
    public void apply(Long uid, FriendApplyReq friendApplyReq) {
        // 判断是否有好友关系
        UserFriend friend = userFriendDao.getByFriend(uid, friendApplyReq.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友了");
        // 判断是否已经自己申请过加对方好友，待审批状态
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, friendApplyReq.getTargetUid());
        if (Objects.nonNull(selfApproving)) {
            // 申请过了则直接返回
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, friendApplyReq.getTargetUid());
            return;
        }
        // 查看对方是否已经申请了加自己好友，如果是则直接同意，不需要再申请对方
        UserApply friendApproving = userApplyDao.getFriendApproving(friendApplyReq.getTargetUid(), uid);
        if (Objects.nonNull(friendApproving)) {
            ((UserFriendService) AopContext.currentProxy()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }
        // 申请入库
        UserApply insert = FriendAdapter.buildFriendApply(uid, friendApplyReq);
        userApplyDao.save(insert);
        // 广播事件
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, insert));
    }

    /**
     * 好友申请批准
     * @param uid
     * @param friendApproveReq
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void applyApprove(Long uid, FriendApproveReq friendApproveReq) {
        UserApply userApply = userApplyDao.getById(friendApproveReq.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getStatus(), "已同意好友申请");
        //同意申请
        userApplyDao.agree(friendApproveReq.getApplyId());
        //创建双方好友关系
        createFriend(uid, userApply.getUid());
        // todo 1.创建一个聊天房间 2.发送一条同意消息。。我们已经是好友了，开始聊天吧
    }

    /**
     * 创建双向好友关系
     * @param uid
     * @param targetUid
     */
    private void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend1, userFriend2));
    }

    /**
     * 获取未读申请数
     * @param uid
     * @return {@link FriendUnreadResp}
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    /**
     * 删除好友
     * @param uid
     * @param targetUid
     */
    @Override
    public void deleteFriend(Long uid, Long targetUid) {
        // 获取双向好友关系数据
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, targetUid);
        if (CollectionUtil.isEmpty(userFriends)) {
            log.info("没有好友关系：{},{}", uid, targetUid);
            return;
        }
        // 删除好友数据
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        // todo 禁用房间
    }

    /**
     * 分页查询好友申请信息
     *
     * @param uid
     * @param pageBaseReq
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq pageBaseReq) {
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid, pageBaseReq.plusPage());
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())) {
            return PageBaseResp.empty();
        }
        //将这些申请列表设为已读
        readApples(uid, userApplyIPage);
        //返回消息
        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    /**
     * 已读消息
     * @param uid
     * @param userApplyIPage
     */
    private void readApples(Long uid, IPage<UserApply> userApplyIPage) {
        List<Long> applyIds = userApplyIPage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApples(uid, applyIds);
    }
}
