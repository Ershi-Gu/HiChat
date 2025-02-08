package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import com.ershi.hichat.common.chat.constant.MsgReplyContent;
import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.ImgMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 图片消息处理器
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    protected void checkMsg(ImgMsgDTO imgMsgDTO, Long roomId, Long uid) {
        // 校验图片宽度、长度不能为负数
        if (imgMsgDTO.getWidth() < 0 || imgMsgDTO.getHeight() < 0) {
            throw new BusinessException("图片宽度、长度不能为负数！");
        }
    }

    @Override
    public void saveMsg(Message msg, ImgMsgDTO imgMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setImgMsgDTO(imgMsgDTO);
        messageDao.updateById(update);
    }

    @Override
    public BaseMsgDTO showMsg(Message msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message replyMessage) {
        return MsgReplyContent.IMG_REPLY_CONTENT;
    }
}
