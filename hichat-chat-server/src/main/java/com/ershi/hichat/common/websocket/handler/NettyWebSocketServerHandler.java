package com.ershi.hichat.common.websocket.handler;

import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.user.domain.enums.WSReqTypeEnum;
import com.ershi.hichat.common.user.domain.vo.request.ws.WSBaseReq;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 帧消息处理器
 * @author Ershi
 * @date 2024/11/24
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            // 读空闲（客户端规定时间内未请求读取消息）
            if (state == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                // todo 用户下线
                // 关闭连接
                ctx.channel().close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取消息内容
        String text = msg.text();
        // 转换成包装类
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:
                System.out.println("请求登录");
                ctx.channel().writeAndFlush(new TextWebSocketFrame("登录成功"));
                break;
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                break;
        }
    }
}
