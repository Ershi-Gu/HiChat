package com.ershi.hichat.common.websocket.service.adapter;

import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import org.springframework.stereotype.Component;

/**
 * ws消息适配器
 *
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class WSAdapter {

    /**
     * 构建ws消息推送体
     * @param msgResp
     * @return {@link WSBaseResp }<{@link ChatMessageResp }>
     */
    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }
}
