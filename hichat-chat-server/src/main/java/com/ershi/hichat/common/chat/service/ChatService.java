package com.ershi.hichat.common.chat.service;

import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.vo.request.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;

/**
 * 聊天功能相关接口
 * @author Ershi
 * @date 2025/01/13
 */
public interface ChatService {
    Long sendMsg(ChatMessageReq chatMessageReq, Long uid);

    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);
}
