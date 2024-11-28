package com.ershi.hichat.common.user.service.handler;

import com.ershi.hichat.common.user.service.WXMsgService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 扫码消息处理器
 *
 * @author Ershi
 * @date 2024/11/25
 */
@Component
public class ScanHandler extends AbstractHandler {

    @Resource
    private WXMsgService wxMsgService;


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
        return wxMsgService.scan(wxMpXmlMessage);
    }

}
