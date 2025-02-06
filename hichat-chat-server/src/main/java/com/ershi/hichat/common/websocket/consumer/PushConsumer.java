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
 * 处理需要使用ws推送的消息，由mq下发任务 <br>
 * 消费模式为广播模式，一条消息会被同一个消费组里面的所有订阅者消费，因为存在多端都需要推送消息的情况
 * @author Ershi
 * @date 2025/02/05
 */
@RocketMQMessageListener(topic = MQConstant.PUSH_TOPIC, consumerGroup = MQConstant.PUSH_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
public class PushConsumer implements RocketMQListener<PushMessageDTO> {
    @Autowired
    private WebSocketService webSocketService;

    /**
     * 从MQ接收到需要ws推送的消息任务后触发
     * @param message
     */
    @Override
    public void onMessage(PushMessageDTO message) {
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            // 推送给指定用户
            case USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
                break;
            // 推送给所有人
            case ALL:
                webSocketService.sendMsgToAllOnline(message.getWsBaseMsg(), null);
                break;
        }
    }
}
