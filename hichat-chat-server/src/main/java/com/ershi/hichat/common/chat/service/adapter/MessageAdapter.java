package com.ershi.hichat.common.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.type.TextMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageStatusEnum;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.domain.vo.request.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /**
     * 批量构建消息返回体
     * @param messages
     * @param receiveUid
     * @return {@link List }<{@link ChatMessageResp }>
     */
    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, Long receiveUid) {
        return messages.stream().map(message -> {
                    ChatMessageResp resp = new ChatMessageResp();
                    resp.setFromUser(buildFromUser(message.getFromUid()));
                    resp.setMessageInfo(buildMessage(message , receiveUid));
                    return resp;
                })
                .sorted(Comparator.comparing(message -> message.getMessageInfo().getSendTime()))//帮前端排好序，更方便它展示
                .collect(Collectors.toList());
    }

    /**
     * 构建消息来源者信息
     * @param fromUid
     * @return {@link ChatMessageResp.UserInfo }
     */
    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }

    /**
     * 构建消息体内容
     * @param message
     * @param receiveUid
     * @return {@link ChatMessageResp.MessageInfo }
     */
    private static ChatMessageResp.MessageInfo buildMessage(Message message, Long receiveUid) {
        ChatMessageResp.MessageInfo messageVO = new ChatMessageResp.MessageInfo();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getMsgHandlerNoNull(message.getType());
        if (Objects.nonNull(msgHandler)) {
            messageVO.setMessageBody(msgHandler.showMsg(message));
        }
        return messageVO;
    }

    /**
     * 构建好友通过后的第一条推送消息
     * @param roomId
     * @return {@link ChatMessageReq }
     */
    public static ChatMessageReq buildFriendAgreeMsg(Long roomId) {
        return ChatMessageReq.builder()
                .roomId(roomId)
                .msgType(MessageTypeEnum.TEXT.getType())
                .messageBody(TextMsgDTO.builder().content("我们已经成为好友了，开始聊天吧。").build())
                .build();
    }
}
