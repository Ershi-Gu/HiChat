package com.ershi.hichat.common.websocket.handler;

import cn.hutool.core.net.url.UrlBuilder;
import com.ershi.hichat.common.websocket.utils.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Optional;

/**
 * http请求头处理器
 *
 * @author Ershi
 * @date 2024/12/01
 */
public class HttpHeaderHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取通道中的数据，并处理HTTP请求 <br>
     *
     * @param ctx 通道处理上下文
     * @param msg 接收到的消息对象
     * @throws Exception 如果处理过程中发生异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断接收到的消息是否为HttpRequest实例
        if (msg instanceof FullHttpRequest) {
            // 对HttpRequest实例进行处理
            FullHttpRequest request = (FullHttpRequest) msg;
            // 解析请求URI并构建UrlBuilder对象，用于后续获取查询参数
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            // 从查询参数中获取token，如果不存在，则默认为空字符串
            String token = Optional.ofNullable(urlBuilder.getQuery()).map(k -> k.get("token")).map(CharSequence::toString).orElse("");
            // 将获取到的token作为属性存储在通道中，以供后续使用
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            // 移除token参数，为了路径后续能够匹配到websocket升级处理器
            request.setUri(urlBuilder.getPath().toString());
        }
        ctx.fireChannelRead(msg);
    }
}
