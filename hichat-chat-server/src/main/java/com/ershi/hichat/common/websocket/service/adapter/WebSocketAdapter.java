package com.ershi.hichat.common.websocket.service.adapter;

import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.enums.WSRespTypeEnum;
import com.ershi.hichat.common.user.domain.vo.response.ws.WSBaseResp;
import com.ershi.hichat.common.user.domain.vo.response.ws.dataclass.WSLoginSuccess;
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
     *
     * @param wxMpQrCodeTicket
     * @return {@link WSBaseResp}<{@link WSLoginUrl}>
     */
    public static WSBaseResp<WSLoginUrl> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> qrCodeUrl = new WSBaseResp<>();
        qrCodeUrl.setType(WSRespTypeEnum.LOGIN_URL.getType());
        qrCodeUrl.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return qrCodeUrl;
    }

    /**
     * 构建token返回
     * @param user
     * @param token
     * @return {@link WSBaseResp}<{@link ?}>
     */
    public static WSBaseResp<?> buildTokenResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> wsLoginSuccessResp = new WSBaseResp<>();
        wsLoginSuccessResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess build = WSLoginSuccess.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .build();
        wsLoginSuccessResp.setData(build);
        return wsLoginSuccessResp;
    }

    /**
     * 构建等待授权通知返回
     * @return {@link WSBaseResp}<{@link ?}>
     */
    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<Object> wsWaitAuthorizeResp = new WSBaseResp<>();
        wsWaitAuthorizeResp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return wsWaitAuthorizeResp;
    }

    /**
     * 构建token失效返回
     * @return {@link WSBaseResp}<{@link ?}>
     */
    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<Object> wsWaitAuthorizeResp = new WSBaseResp<>();
        wsWaitAuthorizeResp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsWaitAuthorizeResp;
    }
}
