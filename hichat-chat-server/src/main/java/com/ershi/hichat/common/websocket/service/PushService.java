package com.ershi.hichat.common.websocket.service;

import com.ershi.hichat.common.common.constant.MQConstant;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;

import java.util.List;

public interface PushService {

    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList);

    public void sendPushMsg(WSBaseResp<?> msg, Long uid);

    public void sendPushMsg(WSBaseResp<?> msg);
}
