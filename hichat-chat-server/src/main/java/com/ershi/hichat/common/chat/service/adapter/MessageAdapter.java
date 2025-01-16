package com.ershi.hichat.common.chat.service.adapter;

import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.enums.MessageStatusEnum;
import com.ershi.hichat.common.chat.domain.vo.request.ChatMessageReq;

/**
 * 消息格式转换器
 * @author Ershi
 * @date 2025/01/14
 */
public class MessageAdapter {

    /**
     * 消息发送请求转换成消息持久化体
     * @param chatMessageReq
     * @param uid
     * @return {@link Message }
     */
    public static Message buildMsgSave(ChatMessageReq chatMessageReq, Long uid) {
        return Message.builder()
                .roomId(chatMessageReq.getRoomId())
                .fromUid(uid)
                .status(MessageStatusEnum.NORMAL.getStatus())
                .type(chatMessageReq.getMsgType())
                .build();
    }
}
