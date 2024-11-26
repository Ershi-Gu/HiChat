package com.ershi.hichat.common.user.service.handler;

import com.ershi.hichat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;

/**
 * 扫码消息处理器
 *
 * @author Ershi
 * @date 2024/11/25
 */
@Component
public class ScanHandler extends AbstractHandler {

    /**
     * 微信授权后的回调服务器URL，用于获取用户信息
     */
    @Value("${wx.mp.callback}")
    private String callback;

    /**
     * 授权 URL
     */
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";


//
//    @Autowired
//    private WxMsgService wxMsgService;

    /**
     * @param wxMpXmlMessage   微信返回的消息体
     * @param map
     * @param wxMpService
     * @param wxSessionManager
     * @return {@link WxMpXmlOutMessage}
     * @throws WxErrorException
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // todo 扫码事件处理
        String code = wxMpXmlMessage.getEventKey();
        String openId = wxMpXmlMessage.getFromUser();
        System.out.println("扫码成功");
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(),
                URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击链接授权：<a href=\"" + authorizeUrl + "\">登录</a>",
                wxMpXmlMessage);
    }

}
