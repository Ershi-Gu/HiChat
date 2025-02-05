package com.ershi.hichat.common.websocket.consumer;

import com.ershi.hichat.common.common.constant.MQConstant;
import com.ershi.hichat.common.websocket.domain.dto.PushMessageDTO;
import com.ershi.hichat.common.websocket.domain.enums.WSPushTypeEnum;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理需要使用ws推送的消息，由mq下发任务
 * @author Ershi
 * @date 2025/02/05
 */
@RocketMQMessageListener(topic = MQConstant.PUSH_TOPIC, consumerGroup = MQConstant.PUSH_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
public class PushConsumer implements RocketMQListener<PushMessageDTO> {
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void onMessage(PushMessageDTO message) {
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            case USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
                break;
            case ALL:
                webSocketService.sendMsgToAllOnline(message.getWsBaseMsg(), null);
                break;
        }
    }
}
