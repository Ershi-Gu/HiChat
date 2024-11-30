package com.ershi.hichat.common.user.service.imp;

import cn.hutool.core.util.StrUtil;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.UserService;
import com.ershi.hichat.common.user.service.WXMsgService;
import com.ershi.hichat.common.user.service.adapter.TextBuilder;
import com.ershi.hichat.common.user.service.adapter.UserAdapter;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Objects;

/**
 * 处理和微信api交互相关逻辑
 * @author Ershi
 * @date 2024/11/27
 */
@Slf4j
@Service
public class WXMsgServiceImpl implements WXMsgService {

    /**
     * 微信授权后的回调服务器URL，用于获取用户信息
     */
    @Value("${wx.mp.callback}")
    private String callback;

    /**
     * 授权 URL
     */
    private static final String WX_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    /**
     * 微信回调接口地址
     */
    private static final String CALL_BACK_INTERFACE = "/wx/portal/public/callBack";

    /**
     * openid过期时间
     */
    public static final Duration OPENID_EXPIRE_TIME = Duration.ofHours(1);

    /**
     * 最多保存openid数
     */
    public static final int OPENID_MAXIMUM_SIZE = 10000;

    /**
     * open_id和登录事件码code的关系
     */
    private static final Cache<String, Integer> WAIT_AUTHORIZE_MAP = Caffeine.newBuilder()
            .maximumSize(OPENID_MAXIMUM_SIZE)
            .expireAfterWrite(OPENID_EXPIRE_TIME)
            .build();

    /**
     * ws 连接业务
     */
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy // 解决 WxMapConfiguration -> SubscribeHandler -> WXMsgServiceImpl -> 循环依赖
    private WxMpService wxMpService;

    /**
     * 处理用户扫码事件
     * @param wxMpXmlMessage
     * @return {@link WxMpXmlOutMessage}
     */
    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        // 获取扫码用户的微信open_id
        String openId = wxMpXmlMessage.getFromUser();
        // 获取登录码
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)){
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StringUtil.isNotBlank(user.getAvatar());
        // 用户已注册并微信授权（用户不是第一次登录）
        if (registered && authorized){
            // todo 登录成功逻辑 => 通过code找到channel推送消息
            webSocketService.scanLoginSuccess(code, user.getId());
        }
        // 用户未注册 => 走注册的逻辑
        if(!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        // 保存open_id和code的映射
        WAIT_AUTHORIZE_MAP.asMap().put(openId, code);
        // 通过ws推送前端，显示正在进行授权
        webSocketService.waitAuthorize(code);
        // 推送链接到微信让用户授权
        String authorizeUrl = String.format(WX_AUTHORIZE_URL, wxMpService.getWxMpConfigStorage().getAppId(),
                URLEncoder.encode(callback + CALL_BACK_INTERFACE));
        return TextBuilder.build("请点击链接授权：<a href=\"" + authorizeUrl + "\">登录</a>",
                wxMpXmlMessage);
    }

    /**
     * 用户授权获取信息
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        // 根据openId查询用户信息
        String openId = userInfo.getOpenid();
        User user = userDao.getByOpenId(openId);
        // 若用户信息不完整则更新数据库数据
        if (StrUtil.isBlank(user.getAvatar()) || StrUtil.isBlank(user.getName())) {
            fillUserInfo(user.getId(), userInfo);
        }
        // 通过code找到用户所属channel进行登录
        Integer code = WAIT_AUTHORIZE_MAP.asMap().remove(openId);
        webSocketService.scanLoginSuccess(code, user.getId());
    }


    /**
     * 填充用户信息
     * @param uid
     * @param userInfo
     */
    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User updateUser = UserAdapter.buildAuthorizeUser(uid, userInfo);
        // todo 重试名称是否重复，若重复则随即名称直到不重复
        userDao.updateById(updateUser);
    }

    /**
     * 获取登录事件码
     * @param wxMpXmlMessage
     * @return {@link Long}
     */
    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            log.error("getEventKey error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }
}
