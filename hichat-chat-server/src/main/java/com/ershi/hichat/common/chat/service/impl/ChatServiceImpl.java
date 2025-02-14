package com.ershi.hichat.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.ershi.hichat.common.chat.dao.ContactDao;
import com.ershi.hichat.common.chat.dao.GroupMemberDao;
import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.dao.RoomFriendDao;
import com.ershi.hichat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.ershi.hichat.common.chat.domain.entity.*;
import com.ershi.hichat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.domain.enums.RoomStatusEnum;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageMarkReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessagePageReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageRecallReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.msg.ChatMessageResp;
import com.ershi.hichat.common.chat.service.ChatService;
import com.ershi.hichat.common.chat.service.adapter.MessageAdapter;
import com.ershi.hichat.common.chat.service.cache.RoomCache;
import com.ershi.hichat.common.chat.service.cache.RoomGroupCache;
import com.ershi.hichat.common.chat.service.strategy.mark.AbstractMsgMarkStrategy;
import com.ershi.hichat.common.chat.service.strategy.mark.MsgMarkFactory;
import com.ershi.hichat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.type.RecallMsgHandler;
import com.ershi.hichat.common.common.annotation.RedissonLock;
import com.ershi.hichat.common.common.event.MessageRecallEvent;
import com.ershi.hichat.common.common.event.MessageSendEvent;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.enums.BlackTypeEnum;
import com.ershi.hichat.common.user.domain.enums.RoleEnum;
import com.ershi.hichat.common.user.service.UserRoleService;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Ershi
 * @date 2025/01/13
 */
@Service
public class ChatServiceImpl implements  ChatService {

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomGroupCache roomGroupCache;
    
    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RecallMsgHandler recallMsgHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发送消息
     * @param chatMessageReq
     * @param uid
     * @return {@link Long }
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq chatMessageReq, Long uid) {
        // 检查用户是否有在该房间聊天的权限
        check(chatMessageReq, uid);
        // 根据消息类型获取对应的处理器
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getMsgHandlerNoNull(chatMessageReq.getMsgType());
        // 检查并持久化消息
        Long msgId = msgHandler.checkAndSaveMsg(chatMessageReq, uid);
        // 发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }

    /**
     * 检查目标用户是否有在目标房间聊天的权限
     * @param chatMessageReq
     * @param uid 发送消息的用户uid
     */
    private void check(ChatMessageReq chatMessageReq, Long uid) {
        // 获取请求发送的房间信息
        Room room = roomCache.get(chatMessageReq.getRoomId());
        AssertUtil.nonNull(room, "房间不存在");
        // 判断单聊状态
        if (room.isRoomFriend()) {
            // 获取单聊房间状态
            RoomFriend roomFriend = roomFriendDao.getByRoomId(chatMessageReq.getRoomId());
            // 判断房间状态
            AssertUtil.equal(RoomStatusEnum.NORMAL.getStatus(), roomFriend.getStatus(), "您已经被对方拉黑");
            // 判断正在使用这个单聊房间的是否是正确的用户
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "你们不是好友哦");
        }
        if (room.isRoomGroup()) {
            // 获取群聊房间状态
            RoomGroup roomGroup = roomGroupCache.get(chatMessageReq.getRoomId());
            // 根据uid获取自己在群聊中的状态信息
            GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(member, "您不在该群中哦");
        }
    }

    /**
     * 获取消息返回体
     * @param msgId
     * @param receiveUid
     * @return {@link ChatMessageResp }
     */
    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        Message msg = messageDao.getById(msgId);
        return getMsgResp(msg, receiveUid);
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
    }

    /**
     * 批量获取消息返回体
     * @param messages
     * @param receiveUid
     * @return {@link List }<{@link ChatMessageResp }>
     */
    public List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        // todo 查询消息标记
        return MessageAdapter.buildMsgResp(messages, receiveUid);
    }

    /**
     * 获取消息列表
     *
     * @param chatMessagePageReq
     * @param receiveUid
     * @return {@link CursorPageBaseResp }<{@link ChatMessageResp }>
     */
    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq chatMessagePageReq, Long receiveUid) {
        // 用用户收件箱最后一条消息id，来限制被踢出的人能看见的最后消息
        Long lastMsgId = getLastMsgId(chatMessagePageReq.getRoomId(), receiveUid);
        // 获取游标翻页消息列表
        CursorPageBaseResp<Message> cursorPage =
                messageDao.getCursorPage(chatMessagePageReq.getRoomId(), chatMessagePageReq, lastMsgId);
        // 如果查询不到就返回空对象
        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        // 组装消息分页返回
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), receiveUid));
    }

    /**
     * 获取用户在该房间收到的最后一条消息id
     * @param roomId
     * @param receiveUid
     * @return {@link Long }
     */
    private Long getLastMsgId(Long roomId, Long receiveUid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号输入错误");
        // 判断是否是全员群，全员群不做限制
        if(room.isAllRoom()) {
            return null;
        }
        AssertUtil.isNotEmpty(receiveUid, "请先登录");
        // 从成员收件箱里面获取该房间该成员读取到的最后一条消息
        Contact contact = contactDao.get(room.getId(), receiveUid);
        AssertUtil.isNotEmpty(contact, "会话数据查询为空");
        return contact.getLastMsgId();
    }

    /**
     * 过滤掉拉黑用户的信息
     * @param memberPage
     */
    @Override
    public void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> memberPage) {
        Set<String> blackMembers = userInfoCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType(), new HashSet<>());
        memberPage.getList().removeIf(member -> blackMembers.contains(member.getFromUser().getUid().toString()));
    }

    /**
     * 撤回消息
     * @param uid
     * @param chatMessageRecallReq
     */
    @Override
    public void recallMsg(Long uid, ChatMessageRecallReq chatMessageRecallReq) {
        // 获取需要撤回的消息
        Message message = messageDao.getById(chatMessageRecallReq.getMsgId());
        // 校验撤回合法性
        checkRecall(uid, message);
        // 执行消息撤回
        recallMsgHandler.recall(uid, message);
        // 推送撤回事件
        ChatMsgRecallDTO chatMsgRecallDTO = ChatMsgRecallDTO.builder()
                .msgId(message.getId())
                .roomId(message.getRoomId())
                .recallUid(uid).build();
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, chatMsgRecallDTO));
    }

    /**
     * 撤回消息合法性校验
     * @param uid
     * @param message
     */
    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息不存在");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "撤回的消息不能再撤回了哦");
        // 超管可以撤回所有人消息
        boolean isAdmin = userRoleService.checkAuth(uid, RoleEnum.ADMIN);
        if (isAdmin) {
            return;
        }
        // todo 判断是否是该房间管理员，是则有权限撤回
        // 判断是否是消息发送者本人
        boolean isSelf = Objects.equals(uid, message.getFromUid());
        AssertUtil.isTrue(isSelf, "撤回失败，您不是该消息的发送者哦");
        // 判断消息是否超过两分钟
        long between = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between < 2, "超过2分钟的消息不能撤回哦");
    }

    /**
     * 对消息进行标记
     * @param uid
     * @param request
     */
    @Override
    @RedissonLock(key = "#uid")
    public void setMsgMark(Long uid, ChatMessageMarkReq chatMessageMarkReq) {
        // 获取消息标记处理器
        AbstractMsgMarkStrategy msgMarkStrategy = MsgMarkFactory.getStrategyNoNull(chatMessageMarkReq.getMarkType());
        AssertUtil.isNotEmpty(msgMarkStrategy, "消息标记类型错误");
        MessageMarkActTypeEnum messageMarkActType = MessageMarkActTypeEnum.of(chatMessageMarkReq.getActType());
        AssertUtil.isNotEmpty(messageMarkActType, "消息标记动作类型错误");
        switch (messageMarkActType) {
            case MARK:
                msgMarkStrategy.mark(uid, chatMessageMarkReq.getMsgId());
                break;
            case UN_MARK:
                msgMarkStrategy.unMark(uid, chatMessageMarkReq.getMsgId());
                break;
        }
    }
}
