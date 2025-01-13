package com.ershi.hichat.common.chat.service.impl;

import com.ershi.hichat.common.chat.domain.vo.request.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.chat.service.ChatService;
import org.springframework.stereotype.Service;

/**
 * @author Ershi
 * @date 2025/01/13
 */
@Service
public class ChatServiceImpl implements  ChatService {
    @Override
    public Long sendMsg(ChatMessageReq chatMessageReq, Long uid) {
        return null;
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long uid) {
        return null;
    }
}
