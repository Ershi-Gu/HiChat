package com.ershi.hichat.common.websocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.common.config.ThreadPoolConfig;
import com.ershi.hichat.common.common.event.UserOnlineEvent;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.enums.ChatActiveStatusEnum;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import com.ershi.hichat.common.user.service.cache.UserLoginCache;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import com.ershi.hichat.common.user.service.LoginService;
import com.ershi.hichat.common.user.service.UserRoleService;
import com.ershi.hichat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import com.ershi.hichat.common.websocket.service.adapter.WebSocketAdapter;
import com.ershi.hichat.common.websocket.utils.NettyUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 专门用于管理websocket相关的逻辑，包括推拉消息
 *
 * @author Ershi
 * @date 2024/11/26
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

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
     * 管理所有在线用户的连接（登录态&游客）=> 管理连接用
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 在线用户及其连接 Uid-Channel => 记录登录态用，实际就是ONLINE_WS_MAP的翻转，便于查询
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

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

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private UserInfoCache userInfoCache;


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
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    /**
     * 如果在线列表不存在，就把当前channel放进在线列表
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    /**
     * 移除保存的用户登录连接
     *
     * @param channel
     */
    @Override
    public void remove(Channel channel) {
        // 删除连接
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        // 判断用户是否多端全部下线
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {  // 已登录用户断连,并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            // todo 发出用户下线通知
        }
    }

    /**
     * 用户下线，用于判断多端是否全部下线
     * @param channel
     * @param uidOptional
     * @return boolean 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
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
        publishLoginSuccess(channel, user, token);
    }

    /**
     * 广播登录成功信息
     *
     * @param channel
     * @param user
     * @param token
     */
    private void publishLoginSuccess(Channel channel, User user, String token) {
        // 登录成功，更新上线列表
        online(channel, user.getId());
        // 更新用户相关在线信息
        updateUserOnlineInfo(channel, user);
        // 获取用户最高权限
        Integer userTopRule = userRoleService.getUserTopRule(user.getId());
        // 推送前端登录成功消息
        sendMsg(channel, WebSocketAdapter.buildTokenResp(user, token, userTopRule));
        // 判断用户是否在线过，防御性编程
        boolean isAlreadyOnline = userInfoCache.isOnline(user.getId());
        if (!isAlreadyOnline) {
            user.setLastOptTime(new Date());
            user.refreshIpInfo(NettyUtil.getAttr(channel, NettyUtil.IP));
            // 发出用户上线通知
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    /**
     * 用户上线后更新当前用户相关在线信息 <br>
     * 1. 在线状态 Active <br>
     * 2. ip <br>
     * 3. 最新上线时间 last_option_time
     *
     * @param channel
     * @param user
     */
    public void updateUserOnlineInfo(Channel channel, User user) {
        user.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        user.setLastOptTime(new Date());
        String ip = channel.attr(NettyUtil.IP).get();
        user.refreshIpInfo(ip);
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
    @Async
    public void authorize(Channel channel, String token) {
        // 判断token是否有效
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            // token有效，通知登录成功，携带用户信息
            User user = userDao.getById(validUid);
            publishLoginSuccess(channel, user, token);
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
     * 发送消息给指定用户
     * @param wsBaseMsg
     * @param uid
     */
    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid){
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    /**
     * 推送消息给所有在线用户，可选择跳过某个用户
     *
     * @param wsBaseResp 推送消息体
     * @param skipUid    需要跳过的用户uid List
     */
    @Override
    public void sendMsgToAllOnline(WSBaseResp<?> wsBaseResp, List<Long> skipUid) {
        // 遍历所有在线用户进行推送
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && skipUid.contains(ext.getUid())) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
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
