package com.ershi.hichat.common.chat.service;

import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageMarkReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessagePageReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageRecallReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;

/**
 * 聊天功能相关接口
 * @author Ershi
 * @date 2025/01/13
 */
public interface ChatService {
    Long sendMsg(ChatMessageReq chatMessageReq, Long uid);

    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq chatMessagePageReq, Long receiveUid);

    void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> memberPage);

    void recallMsg(Long uid, ChatMessageRecallReq chatMessageRecallReq);

    void setMsgMark(Long uid, ChatMessageMarkReq chatMessageMarkReq);
}
