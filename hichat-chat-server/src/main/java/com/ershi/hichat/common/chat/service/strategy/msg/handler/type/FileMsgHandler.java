package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.FileMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.common.exception.BusinessException;
import com.ershi.hichat.common.common.exception.SystemCommonErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 文件消息处理器
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    /**
     * 文件扩展检查
     * @param messageBody
     * @param roomId
     * @param uid
     */
    @Override
    protected void checkMsg(FileMsgDTO messageBody, Long roomId, Long uid) {
        // 获取文件名，查看是否带后缀
        if (!messageBody.getFileName().contains(".")) {
            throw new BusinessException("文件要带后缀名！");
        }
    }

    /**
     * 文件參數額外信息保存
     *
     * @param msg
     * @param fileMsgDTO
     */
    @Override
    public void saveMsg(Message msg, FileMsgDTO fileMsgDTO) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setFileMsg(fileMsgDTO);
        messageDao.updateById(update);
    }
}
