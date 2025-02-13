package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.chat.domain.dto.ChatMessageMarkDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 消息标记事件
 * @author Ershi
 * @date 2025/02/13
 */
@Getter
public class MessageMarkEvent extends ApplicationEvent {

    private final ChatMessageMarkDTO chatMessageMarkDTO;

    public MessageMarkEvent(Object source, ChatMessageMarkDTO chatMessageMarkDTO) {
        super(source);
        this.chatMessageMarkDTO = chatMessageMarkDTO;
    }

}
