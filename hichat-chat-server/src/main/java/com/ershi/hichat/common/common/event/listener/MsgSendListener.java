package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.constant.MQConstant;
import com.ershi.hichat.common.common.domain.dto.MsgSendMessageDTO;
import com.ershi.hichat.common.common.event.MessageSendEvent;
import com.ershi.hichat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 发送消息相关事件监听者
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class MsgSendListener {

    @Autowired
    private MQProducer mqProducer;

    /**
     * 消息路由到mq
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }
}
