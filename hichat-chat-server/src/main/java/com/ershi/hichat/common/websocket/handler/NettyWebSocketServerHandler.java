package com.ershi.hichat.common.websocket.handler;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.user.domain.enums.WSReqTypeEnum;
import com.ershi.hichat.common.user.domain.vo.request.ws.WSBaseReq;
import com.ershi.hichat.common.websocket.service.WebSocketService;
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

    /**
     * ws相关业务处理
     */
    private WebSocketService webSocketService;

    /**
     * 当一个ws连接进来时，对它进行临时保存
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    /**
     * 用户线程发出事件后触发该方法
     * @param ctx channel上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            // 读空闲（客户端规定时间内未请求读取消息）
            // todo 心跳处理
            if (state == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                // todo 用户下线
                // 关闭连接
                ctx.channel().close();
            }
        }
    }

    /**
     * 读取客户端发送的消息
     * @param ctx 通道上下文
     * @param msg 消息体
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取消息内容
        String text = msg.text();
        // 转换成包装类
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:
                webSocketService.handlerLoginReq(ctx.channel());
                break;
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                break;
        }
    }
}
