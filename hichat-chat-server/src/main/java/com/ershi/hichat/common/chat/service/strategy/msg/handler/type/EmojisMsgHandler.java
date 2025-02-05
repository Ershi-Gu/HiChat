package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.EmojisMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 表情消息处理器
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }

    @Override
    protected void checkMsg(EmojisMsgDTO messageBody, Long roomId, Long uid) {

    }

    /**
     * emoji額外信息保存
     *
     * @param msg
     * @param emojisMsgDTO
     */
    @Override
    public void saveMsg(Message msg, EmojisMsgDTO emojisMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setEmojisMsgDTO(emojisMsgDTO);
        messageDao.updateById(update);
    }

    @Override
    public BaseMsgDTO showMsg(Message msg) {
        return msg.getExtra().getEmojisMsgDTO();
    }
}
