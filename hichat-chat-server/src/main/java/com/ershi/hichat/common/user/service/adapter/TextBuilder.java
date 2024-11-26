package com.ershi.hichat.common.user.service.adapter;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public class TextBuilder {

    public static WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage) {
        WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
        return m;
    }
}
