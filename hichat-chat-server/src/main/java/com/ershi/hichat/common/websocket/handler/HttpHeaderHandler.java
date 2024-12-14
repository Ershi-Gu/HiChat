package com.ershi.hichat.common.websocket.handler;

import cn.hutool.core.net.url.UrlBuilder;
import com.ershi.hichat.common.websocket.utils.NettyUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * http请求头处理器
 *
 * @author Ershi
 * @date 2024/12/01
 */
@ChannelHandler.Sharable
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
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            // 获取token
            String token = Optional
                    .ofNullable(urlBuilder.getQuery()).
                    map(k -> k.get("token")).
                    map(CharSequence::toString).
                    orElse("");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            // 移除token参数，为了路径后续能够匹配到websocket升级处理器
            request.setUri(urlBuilder.getPath().toString());

            // 获取ip
            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            if (StringUtils.isEmpty(ip)) {//如果没经过nginx，就直接获取远端地址
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            ctx.fireChannelRead(request);

        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
