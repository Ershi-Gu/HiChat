package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import com.ershi.hichat.common.chat.constant.MsgReplyContent;
import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.RecallMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.type.TextMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 撤回消息处理器
 *
 * @author Ershi
 * @date 2025/02/07
 */
@Component
public class RecallMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private MessageDao messageDao;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {}

    @Override
    protected void saveMsg(Message message, TextMsgDTO messageBody) {
        throw new UnsupportedOperationException("撤回消息不需要保存");

    }

    @Override
    public BaseMsgDTO showMsg(Message msg) {
        RecallMsgDTO recallInfo = msg.getExtra().getRecallMsgDTO();
        // 获取撤回者信息
        User userInfo = userInfoCache.get(recallInfo.getRecallUid());
        // 构建返回
        if (!Objects.equals(recallInfo.getRecallUid(), msg.getFromUid())) {
            return TextMsgDTO.builder().content("管理员\"" + userInfo.getName() + "\"撤回了一条成员消息").build();
        }
        return TextMsgDTO.builder().content("\"" + userInfo.getName() + "\"撤回了一条消息").build();
    }

    @Override
    public Object showReplyMsg(Message replyMessage) {
        return MsgReplyContent.RECALL_REPLY_CONTENT;
    }


    /**
     * 撤回消息
     *
     * @param uid
     * @param message
     * @return {@link Message }
     */
    public RecallMsgDTO recall(Long uid, Message message) {
        MessageExtra extra = message.getExtra();
        RecallMsgDTO recallMsgDTO = RecallMsgDTO.builder()
                .recallUid(uid)
                .recallTime(new Date())
                .build();
        extra.setRecallMsgDTO(recallMsgDTO);
        // 更新被撤回的消息，更新其类型以及扩展信息
        Message update = Message.builder()
                .id(message.getId())
                .type(MessageTypeEnum.RECALL.getType())
                .extra(extra)
                .build();
        messageDao.updateById(update);
        return recallMsgDTO;
    }
}
