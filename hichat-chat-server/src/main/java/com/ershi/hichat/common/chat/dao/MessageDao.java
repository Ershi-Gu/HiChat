package com.ershi.hichat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.enums.MessageStatusEnum;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessagePageReq;
import com.ershi.hichat.common.chat.mapper.MessageMapper;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {


    /**
     * 获取游标消息列表
     * @param roomId 房间id
     * @param chatMessagePageReq 游标翻页请求体，包含游标
     * @param lastMsgId 该用户收件箱中最后一条读取到的消息，用于限制踢出群后最后阅读的消息
     * @return {@link CursorPageBaseResp }<{@link Message }>
     */
    public CursorPageBaseResp<Message> getCursorPage(Long roomId, ChatMessagePageReq chatMessagePageReq, Long lastMsgId) {
        return CursorUtils.getCursorPageByMysql(this, chatMessagePageReq, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        },Message::getId);
    }

    /**
     * 与回复消息之间相差的条数
     *
     * @param roomId
     * @param msgId
     * @param replyMsgId
     * @return {@link Integer } 计算出的结果为当前房间 msgId 到 replyMsgId 之间的条数，包括 replyMsgId 本身
     */
    public Integer getGapCount(Long roomId, Long msgId, Long replyMsgId) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, replyMsgId)
                .le(Message::getId, msgId)
                .count();
    }

    /**
     * 通过用户阅读到的最后消息时间查询未读数
     * @param roomId
     * @param readTime
     * @return {@link Integer }
     */
    public Integer getUnReadCount(Long roomId, Date readTime) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Objects.nonNull(readTime), Message::getCreateTime, readTime)
                .count();
    }
}
