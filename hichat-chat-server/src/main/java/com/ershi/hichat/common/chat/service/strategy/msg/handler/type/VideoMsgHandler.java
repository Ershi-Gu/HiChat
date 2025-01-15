package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.VideoMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 视频消息处理器
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class VideoMsgHandler extends AbstractMsgHandler<VideoMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    protected void checkMsg(VideoMsgDTO messageBody, Long roomId, Long uid) {

    }

    @Override
    public void saveMsg(Message msg, VideoMsgDTO videoMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setVideoMsg(videoMsgDTO);
        messageDao.updateById(update);
    }
}
