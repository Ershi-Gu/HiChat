package com.ershi.hichat.common.chat.service.strategy.mark;

import com.ershi.hichat.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;


/**
 * 点踩标记策略类
 * @author Ershi
 * @date 2025/02/13
 */
@Component
public class DisLikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        // 同时取消点赞的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
    }

}
