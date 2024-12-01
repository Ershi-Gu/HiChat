package com.ershi.hichat.common.websocket.handler;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.user.domain.enums.WSReqTypeEnum;
import com.ershi.hichat.common.user.domain.vo.request.ws.WSBaseReq;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import com.ershi.hichat.common.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;


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
     * 当一个用户通过ws连接进来时，对连接进行临时保存
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }

    /**
     * 用户关闭连接，下线相关数据
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffline(ctx.channel());
    }

    /**
     * 处理用户事件，用户线程发出事件后触发该方法
     * @param ctx channel上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳检查
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 获取心跳检查状态
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE) {
                // 用户下线，同时关闭连接
                userOffline(ctx.channel());
            }
        }
        // 握手认证
        else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 从channel中获取保存的token数据
            String token = NettyUtil.get(ctx.channel(), NettyUtil.TOKEN);
            if (Strings.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
        }

    }

    /**
     * 用户下线操作
     * @param channel
     */
    private void userOffline(Channel channel) {
        webSocketService.remove(channel);
        channel.close();
    }

    /**
     * 读取客户端发送的消息
     * @param ctx 通道上下文
     * @param msg 消息体
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        // 根据不同类型的消息请求做不同的处理
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case LOGIN:
                webSocketService.handlerLoginReq(ctx.channel());
                break;
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                webSocketService.authorize(ctx.channel(), wsBaseReq.getData());
                break;
        }
    }
}
