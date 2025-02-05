package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import cn.hutool.core.collection.CollectionUtil;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.TextMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.service.RoomGroupService;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.common.exception.BusinessException;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 文本消息处理器
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserInfoCache userInfoCache;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    /**
     * 文本消息合法性检查
     * @param messageBody
     * @param roomId
     * @param uid
     */
    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {
        // todo 回复消息校验
        // 如果是回复消息，校验回复消息是否存在以及合法性
//        checkReplyMsg(messageBody, roomId);
        // todo 艾特用户校验
        // 如果有艾特用户，需要校验艾特合法性
//        checkAtMsg(messageBody, roomId, uid);
    }

    /**
     * 检查消息回复操作的合法性
     * @param messageBody
     * @param roomId
     */
    private void checkReplyMsg(TextMsgDTO messageBody, Long roomId) {
        if (Objects.nonNull(messageBody.getReplyMsgId())) {
            Message replyMsg = messageDao.getById(messageBody.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复的消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), roomId, "只能回复相同会话内的消息");
        }
    }

    /**
     * 艾特用户操作合法性校验
     * @param messageBody
     * @param roomId
     * @param uid
     */
    private void checkAtMsg(TextMsgDTO messageBody, Long roomId, Long uid) {
        if (CollectionUtil.isNotEmpty(messageBody.getAtUidList())) {
            // 前端传入的艾特用户列表可能会重复，需要去重，支持多次艾特一个用户，但是只显示一次
            List<Long> atUidList = messageBody.getAtUidList().stream().distinct().collect(Collectors.toList());
            Map<Long, User> userMap = userInfoCache.getBatch(atUidList);
            // 校验艾特的用户是否错在
            // 如果艾特用户不存在，userInfoCache返回的map中依然存在该key，但是value为null，需要过滤掉再校验
            long batchCount = userMap.values().stream().filter(Objects::nonNull).count();
            AssertUtil.equal((long)atUidList.size(), batchCount, "@用户不存在");
            // 检查是否是艾特全体成员
            boolean atAll = messageBody.getAtUidList().contains(0L);
            if (atAll) { // 若是则判断用户在该群聊中的权限

            }
        }
    }

    /**
     * 保存文本消息额外参数内容
     *
     * @param msg
     * @param textMsgDTO
     */
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
}
