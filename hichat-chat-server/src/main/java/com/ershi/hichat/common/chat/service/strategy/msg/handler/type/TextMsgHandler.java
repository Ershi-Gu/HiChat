package com.ershi.hichat.common.chat.service.strategy.msg.handler.type;

import cn.hutool.core.collection.CollectionUtil;

import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.MessageExtra;
import com.ershi.hichat.common.chat.domain.entity.msg.type.TextMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageStatusEnum;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.domain.vo.response.msg.TextMsgResp;
import com.ershi.hichat.common.chat.service.RoomGroupService;
import com.ershi.hichat.common.chat.service.adapter.MessageAdapter;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.common.exception.BusinessException;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserRole;
import com.ershi.hichat.common.user.domain.enums.RoleEnum;
import com.ershi.hichat.common.user.service.UserRoleService;
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
 *
 * @author Ershi
 * @date 2025/01/15
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    protected MessageTypeEnum getMessageTypeEnum() {
        return MessageTypeEnum.TEXT;
    }

    /**
     * 文本消息合法性检查
     *
     * @param messageBody
     * @param roomId
     * @param uid
     */
    @Override
    protected void checkMsg(TextMsgDTO messageBody, Long roomId, Long uid) {
        // 如果是回复消息，校验回复消息是否存在以及合法性
        checkReplyMsg(messageBody, roomId);
        // 如果有艾特用户，需要校验艾特合法性
        checkAtMsg(messageBody, roomId, uid);
    }

    /**
     * 检查消息回复操作的合法性
     *
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
     *
     * @param messageBody
     * @param roomId
     * @param uid
     */
    private void checkAtMsg(TextMsgDTO messageBody, Long roomId, Long uid) {
        if (CollectionUtil.isNotEmpty(messageBody.getAtUidList())) {
            // 前端传入的艾特用户列表可能会重复，需要去重，支持多次艾特一个用户，但是只显示一次
            List<Long> atUidList = messageBody.getAtUidList().stream().distinct().collect(Collectors.toList());
            Long atUidNum = (long) atUidList.size();
            Map<Long, User> userMap = userInfoCache.getBatch(atUidList);
            // 检查是否是艾特全体成员
            boolean atAll = messageBody.getAtUidList().contains(0L);
            if (atAll) { // 若是则判断用户在该群聊中的权限
                // todo 房间管理员还需要设计
                AssertUtil.isTrue(userRoleService.checkAuth(uid, RoleEnum.ADMIN), "没有权限");
                atUidNum--;
            }
            // 校验艾特的用户是否存在
            // 如果艾特用户不存在，userInfoCache返回的map中依然存在该key，但是value为null，需要过滤掉再校验；同时过滤掉0L，表示艾特全体成员
            long batchCount = userMap.values().stream().filter(Objects::nonNull).count();
            AssertUtil.equal(atUidNum, batchCount, "@用户不存在");
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
        // 保存消息内容
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setTextMsgDTO(textMsgDTO);
        // 判断是否是回复消息，若是则计算出与回复消息相差条数
        if (Objects.nonNull(textMsgDTO.getReplyMsgId())) {
            Integer gapCountToReply = messageDao.getGapCount(msg.getRoomId(), msg.getId(), textMsgDTO.getReplyMsgId());
            textMsgDTO.setGapCountToReply(gapCountToReply);
        }
        // 去重艾特的uid
        textMsgDTO.setAtUidList(textMsgDTO.getAtUidList().stream().distinct().collect(Collectors.toList()));
        messageDao.updateById(update);
    }

    @Override
    public BaseMsgDTO showMsg(Message msg) {
        TextMsgDTO textMsgDTO = msg.getExtra().getTextMsgDTO();
        // 构建文本消息返回体
        TextMsgResp textMsgResp = TextMsgResp.builder()
                .content(textMsgDTO.getContent())
                .build();
        // 回复消息的展示
        // 1. 获取回复的那条消息
        Optional<Message> reply = Optional.ofNullable(textMsgDTO.getReplyMsgId())
                .map(messageDao::getById)
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
        if (!reply.isPresent()) {
            return textMsgResp;
        }
        // 2. 如果回复消息存在，组装回复消息
        Message replyMessage = reply.get();
        User replyUserInfo = userInfoCache.get(replyMessage.getFromUid());
        textMsgResp.setReply(MessageAdapter.buildReplyMessage(textMsgDTO, replyMessage, replyUserInfo));
        // 设置艾特的uid
        textMsgResp.setAtUidList(textMsgDTO.getAtUidList());
        return textMsgResp;
    }

    @Override
    public Object showReplyMsg(Message replyMessage) {
        return replyMessage.getExtra().getTextMsgDTO().getContent();
    }
}
