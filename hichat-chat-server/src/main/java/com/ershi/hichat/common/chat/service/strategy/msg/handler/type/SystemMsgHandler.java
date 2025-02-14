package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.TextMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * 系统消息处理器
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class SystemMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.SYSTEM;
    }

    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {

    }

    @Override
    public void saveMsg(Message msg, TextMsgDTO textMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setTextMsgDTO(textMsgDTO);
        messageDao.updateById(update);
    }

    @Override
    public BaseMsgDTO showMsg(Message msg) {
        return msg.getExtra().getTextMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message replyMessage) {
        return replyMessage.getExtra().getTextMsgDTO().getContent();
    }

    @Override
    public String showMsgOnContact(Message message) {
        return message.getExtra().getTextMsgDTO().getContent();
    }

}
