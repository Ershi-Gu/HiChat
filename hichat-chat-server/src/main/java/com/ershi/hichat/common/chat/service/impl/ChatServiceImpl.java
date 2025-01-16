package com.ershi.hichat.common.chat.service.impl;

import com.ershi.hichat.common.chat.dao.GroupMemberDao;
import com.ershi.hichat.common.chat.dao.RoomFriendDao;
import com.ershi.hichat.common.chat.domain.entity.GroupMember;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import com.ershi.hichat.common.chat.domain.enums.RoomStatusEnum;
import com.ershi.hichat.common.chat.domain.vo.request.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.chat.service.ChatService;
import com.ershi.hichat.common.chat.service.cache.RoomCache;
import com.ershi.hichat.common.chat.service.cache.RoomGroupCache;
import com.ershi.hichat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.common.event.MessageSendEvent;
import com.ershi.hichat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RoomFriendDao roomFriendDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

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
        // todo 事件接收处理
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

    // todo 消息返回展示
    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long uid) {
        return null;
    }
}
