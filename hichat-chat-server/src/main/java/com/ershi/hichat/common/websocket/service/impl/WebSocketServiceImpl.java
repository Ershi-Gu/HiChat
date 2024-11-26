package com.ershi.hichat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.user.domain.vo.response.ws.WSBaseResp;
import com.ershi.hichat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import com.ershi.hichat.common.websocket.service.adapter.WebSocketAdapter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门用于管理websocket相关的逻辑，包括推拉消息
 *
 * @author Ershi
 * @date 2024/11/26
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Resource
    private WxMpService wxMpService;

    /**
     * 管理所有在线用户的连接（登录态/游客）
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 登录码过期时间
     */
    public static final Duration CODE_EXPIRE_TIME = Duration.ofHours(1);
    /**
     * 最多保存登录码数
     */
    public static final int CODE_MAXIMUM_SIZE = 10000;

    /**
     * 临时保存连接进来时的code和channel的映射关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(CODE_MAXIMUM_SIZE)
            .expireAfterWrite(CODE_EXPIRE_TIME)
            .build();

    /**
     * 保存ws连接
     *
     * @param channel
     */
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 处理请求登录二维码
     *
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handlerLoginReq(Channel channel) {
        // 生成随机登录码
        Integer code = generateLoginCode(channel);
        // 请求微信带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket =
                wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) CODE_EXPIRE_TIME.getSeconds());
        // 将二维码推送前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // todo 用户下线
    }

    /**
     * 主动推送消息
     * @param channel
     * @param msg
     */
    private <T> void sendMsg(Channel channel, WSBaseResp<T> msg) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(msg)));
    }

    /**
     * 生成临时登录码
     * @param channel
     * @return {@link Integer}
     */
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
