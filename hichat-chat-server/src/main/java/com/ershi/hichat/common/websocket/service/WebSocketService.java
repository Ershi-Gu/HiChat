package com.ershi.hichat.common.websocket.service;

import io.netty.channel.Channel;

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
}
