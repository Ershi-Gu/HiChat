package com.ershi.hichat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 消息发送事件
 * @author Ershi
 * @date 2025/01/14
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {
    private final Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
