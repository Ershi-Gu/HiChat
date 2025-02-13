package com.ershi.hichat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.chat.domain.entity.MessageMark;
import com.ershi.hichat.common.chat.domain.enums.MessageMarkStatusEnum;
import com.ershi.hichat.common.chat.mapper.MessageMarkMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-13
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    /**
     * @param uid
     * @param msgId
     * @param markType
     * @return {@link MessageMark }
     */
    public MessageMark get(Long uid, Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getUid, uid)
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, markType)
                .one();
    }

    public Integer getMarkCount(Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, markType)
                .eq(MessageMark::getStatus, MessageMarkStatusEnum.DO.getType())
                .count();
    }
}
