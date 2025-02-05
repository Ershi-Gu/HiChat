package com.ershi.hichat.common.websocket.service.impl;

import com.ershi.hichat.common.common.constant.MQConstant;
import com.ershi.hichat.common.websocket.domain.dto.PushMessageDTO;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import com.ershi.hichat.common.websocket.service.PushService;
import com.ershi.hichat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * WS消息推送服务
 * @author Ershi
 * @date 2025/02/05
 */
@Service
public class PushServiceImpl implements PushService {

    @Autowired
    private MQProducer mqProducer;

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uidList, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
    }

    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }
}
