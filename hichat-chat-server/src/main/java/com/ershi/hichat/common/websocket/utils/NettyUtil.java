package com.ershi.hichat.common.websocket.utils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;


/**
 * Netty 工具类
 * @author Ershi
 * @date 2024/12/01
 */
public class NettyUtil {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");

    public static AttributeKey<Long> UID = AttributeKey.valueOf("uid");

    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    /**
     * 向channel中设置属性
     *
     * @param channel 属性的Channel对象
     * @param attributeKey 属性键，用于唯一标识Channel中存储的数据，类型为 T
     * @param data 属性值
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        // 获取Channel中与attributeKey关联的Attribute对象
        Attribute<T> attr = channel.attr(attributeKey);
        // 设置Attribute对象的值为data
        attr.set(data);
    }

    /**
     * 从channel中获取属性
     * @param channel
     * @param attributeKey 属性键
     * @return {@link T}
     */
    public static <T> T get(Channel channel, AttributeKey<T> attributeKey) {
        return channel.attr(attributeKey).get();
    }

}
