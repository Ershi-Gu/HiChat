package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.chat.domain.dto.ChatMsgRecallDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageRecallEvent extends ApplicationEvent {

    private final ChatMsgRecallDTO chatMsgRecallDTO;

    public MessageRecallEvent(Object source, ChatMsgRecallDTO chatMsgRecallDTO) {
        super(source);
        this.chatMsgRecallDTO = chatMsgRecallDTO;
    }
}
