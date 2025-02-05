package com.ershi.hichat.common.websocket.service;

import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author Ershi
 * @date 2024/11/26
 */
public interface WebSocketService {

    void connect(Channel channel);

    void handlerLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String token);

    void sendMsgToAllOnline(WSBaseResp<?> wsBaseResp, List<Long> skipUid);

    void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid);
}
