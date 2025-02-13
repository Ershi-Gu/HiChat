package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.dao.MessageMarkDao;
import com.ershi.hichat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.cache.RoomCache;
import com.ershi.hichat.common.common.event.MessageMarkEvent;
import com.ershi.hichat.common.user.domain.enums.IdempotentSourceEnum;
import com.ershi.hichat.common.user.domain.enums.ItemEnum;
import com.ershi.hichat.common.user.service.UserBackpackService;
import com.ershi.hichat.common.websocket.service.PushService;
import com.ershi.hichat.common.websocket.service.adapter.WSMsgAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息标记事件监听器
 *
 * @author Ershi
 * @date 2025/02/13
 */
@Slf4j
@Component
public class MessageMarkListener {

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserBackpackService iUserBackpackService;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void checkAndAwardLikeBadge(MessageMarkEvent event) {
        ChatMessageMarkDTO chatMessageMarkDTO = event.getChatMessageMarkDTO();
        Message msg = messageDao.getById(chatMessageMarkDTO.getMsgId());
        if (!Objects.equals(msg.getType(), MessageTypeEnum.TEXT.getType())) { // 普通消息被点赞才满足点赞升级发放徽章
            return;
        }
        // 获取消息被标记次数
        Integer markCount = messageMarkDao.getMarkCount(chatMessageMarkDTO.getMsgId(), chatMessageMarkDTO.getMarkType());
        // 判断是否满足点赞徽章升级条件
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(chatMessageMarkDTO.getMarkType());
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        // 尝试给用户发送一张徽章
        if (MessageMarkTypeEnum.LIKE.getType().equals(chatMessageMarkDTO.getMarkType())) {
            iUserBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentSourceEnum.MSG_ID, msg.getId().toString());
        }
    }

    /**
     * 推送标记通知给该房间所有在线用户
     *
     * @param messageMarkEvent
     */
    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent messageMarkEvent) {
        ChatMessageMarkDTO chatMessageMarkDTO = messageMarkEvent.getChatMessageMarkDTO();
        Integer markCount = messageMarkDao.getMarkCount(chatMessageMarkDTO.getMsgId(), chatMessageMarkDTO.getMarkType());
        pushService.sendPushMsg(WSMsgAdapter.buildMsgMarkSend(chatMessageMarkDTO, markCount));
    }
}
