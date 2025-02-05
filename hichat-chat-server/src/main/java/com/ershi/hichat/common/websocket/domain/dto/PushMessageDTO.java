package com.ershi.hichat.common.websocket.domain.dto;

import com.ershi.hichat.common.websocket.domain.enums.WSPushTypeEnum;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 推送给用户的消息对象
 * @author Ershi
 * @date 2025/02/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushMessageDTO implements Serializable {

    /**
     * 推送的ws消息
     */
    private WSBaseResp<?> wsBaseMsg;

    /**
     * 推送的uid
     */
    private List<Long> uidList;

    /**
     * 推送类型 1个人 2全员
     *
     * @see com.ershi.hichat.common.websocket.domain.enums.WSPushTypeEnum
     */
    private Integer pushType;

    public PushMessageDTO(Long uid, WSBaseResp<?> wsBaseMsg) {
        this.uidList = Collections.singletonList(uid);
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
    }

    public PushMessageDTO(List<Long> uidList, WSBaseResp<?> wsBaseMsg) {
        this.uidList = uidList;
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.USER.getType();
    }

    public PushMessageDTO(WSBaseResp<?> wsBaseMsg) {
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.ALL.getType();
    }
}
