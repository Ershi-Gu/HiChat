package com.ershi.hichat.common.websocket.service.adapter;

import com.ershi.hichat.common.user.domain.enums.WSRespTypeEnum;
import com.ershi.hichat.common.user.domain.vo.response.ws.WSBaseResp;
import com.ershi.hichat.common.user.domain.vo.response.ws.dataclass.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * WebSocket 模块相关类型转换器
 *
 * @author Ershi
 * @date 2024/11/26
 */
public class WebSocketAdapter {

    /**
     * 将WxMpQrCodeTicket二维码转换WSBaseResp
     * @param wxMpQrCodeTicket
     * @return {@link WSBaseResp}<{@link WSLoginUrl}>
     */
    public static WSBaseResp<WSLoginUrl> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> qrCodeUrl = new WSBaseResp<>();
        qrCodeUrl.setType(WSRespTypeEnum.LOGIN_URL.getType());
        qrCodeUrl.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return qrCodeUrl;
    }
}
