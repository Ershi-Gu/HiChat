package com.ershi.hichat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.enums.MessageStatusEnum;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessagePageReq;
import com.ershi.hichat.common.chat.mapper.MessageMapper;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

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
}
