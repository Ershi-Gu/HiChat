package com.ershi.hichat.common.chat.consumer;


import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.dao.RoomDao;
import com.ershi.hichat.common.chat.dao.RoomFriendDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.chat.service.ChatService;
import com.ershi.hichat.common.chat.service.cache.GroupMemberCache;
import com.ershi.hichat.common.chat.service.cache.HotRoomCache;
import com.ershi.hichat.common.chat.service.cache.RoomCache;
import com.ershi.hichat.common.common.constant.MQConstant;
import com.ershi.hichat.common.common.domain.dto.MsgSendMessageDTO;
import com.ershi.hichat.common.websocket.service.PushService;
import com.ershi.hichat.common.websocket.service.adapter.WSAdapter;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 发送消息更新房间收信箱，并同步给房间成员信箱
 * @author Ershi
 * @date 2025/02/05
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    protected RoomCache roomCache;

    @Autowired
    private HotRoomCache hotRoomCache;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private ChatService chatService;

    @Autowired
    private PushService pushService;

    @Override
    public void onMessage(MsgSendMessageDTO dto) {
        Message message = messageDao.getById(dto.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        ChatMessageResp msgResp = chatService.getMsgResp(message.getId(), null);
        // 更新本条消息所属房间最新记录消息点
        roomDao.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
        roomCache.delete(room.getId());
        if (room.isHotRoom()) { // 热门群聊额外使用Redis存储更新时间，这样在热点消息聚合时，直接去Redis即可拿到最新消息
            // 更新热门群聊时间-redis
            hotRoomCache.refreshActiveTime(room.getId(), message.getCreateTime());
            // 推送给人们群聊中的指定用户
            List<Long> memberUidList = getMemberUidList(room);
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);
        } else {
            List<Long> memberUidList = new ArrayList<>();
            if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) {//普通群聊推送所有群成员
                memberUidList = getMemberUidList(room);
            } else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())) {//单聊对象
                // 对单人推送
                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            // todo 更新所有群成员收件箱的最新会话时间
//            contactDao.refreshOrCreateActiveTime(room.getId(), memberUidList, message.getId(), message.getCreateTime());
            // 推送房间成员
            pushService.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);
        }
    }

    /**
     * 获取房间成员uid列表
     * @param room
     * @return {@link List }<{@link Long }>
     */
    private List<Long> getMemberUidList(Room room) {
        return groupMemberCache.getMemberUidList(room.getId());
    }

}
