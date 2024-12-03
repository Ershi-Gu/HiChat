package com.ershi.hichat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.vo.response.ws.WSBaseResp;
import com.ershi.hichat.common.user.service.LoginService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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

    /**
     * 微信业务处理器
     */
    @Autowired
    @Lazy // 解决 wxMpConfiguration -> scanHandler -> WXMsgServiceImpl -> webSocketServiceImpl 循环依赖
    private WxMpService wxMpService;

    @Autowired
    private UserDao userDao;

    /**
     * 登录业务处理
     */
    @Autowired
    private LoginService loginService;

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

    /**
     * 移除保存的用户登录连接
     *
     * @param channel
     */
    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // todo 用户下线
    }

    /**
     * 扫码登录成功，推送前端登录成功信息
     *
     * @param code
     * @param uid
     */
    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        // 通过code获取需要登录的连接
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) { // 如果找不到连接直接返回，静默处理
            return;
        }
        User user = userDao.getById(uid);
        // 移除code和channel的映射
        WAIT_LOGIN_MAP.invalidate(code);
        // 调用登录模块获取token
        String token = loginService.login(uid);
        // 推送用户登录成功通知
        loginSuccess(channel, user, token);
    }

    /**
     * 通用推送前端登录成功信息
     * @param channel
     * @param user
     * @param token
     */
    private void loginSuccess(Channel channel, User user, String token) {
        // 保存channel-uid映射
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        // todo 推送用户上线成功事件
        // 推送前端登录成功消息
        sendMsg(channel, WebSocketAdapter.buildTokenResp(user, token));
    }

    /**
     * 推送前端等待授权消息
     *
     * @param code
     */
    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    /**
     * 携带token的登录鉴权
     *
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        // 判断token是否有效
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            // token有效，通知登录成功，携带用户信息
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
        } else {
            // 通知前端token失效，请删除
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    /**
     * 主动推送消息
     *
     * @param channel
     * @param msg
     */
    private <T> void sendMsg(Channel channel, WSBaseResp<T> msg) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(msg)));
    }

    /**
     * 生成临时登录码
     *
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
