package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.chat.dao.RoomDao;
import com.ershi.hichat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.service.cache.GroupMemberCache;
import com.ershi.hichat.common.common.event.MessageRecallEvent;
import com.ershi.hichat.common.websocket.service.PushService;
import com.ershi.hichat.common.websocket.service.adapter.WSMsgAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * 消息撤回监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageRecallListener {

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private PushService pushService;

    @Autowired
    private GroupMemberCache groupMemberCache;

    /**
     * 推送撤回消息给
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToUser(MessageRecallEvent event) {
        // 获取撤回消息所在房间成员信息
        ChatMsgRecallDTO chatMsgRecallDTO = event.getChatMsgRecallDTO();
        Room room = roomDao.getById(chatMsgRecallDTO.getRoomId());
        List<Long> memberUidList = groupMemberCache.getMemberUidList(room.getId());
        // 推送撤回消息到该房间成员
        pushService.sendPushMsg(WSMsgAdapter.buildMsgRecall(chatMsgRecallDTO), memberUidList);
    }
}
