package com.ershi.hichat.common.user.domain.vo.response.ws;

import lombok.Data;

/**
 * ws 服务端基本推送体
 * @author Ershi
 * @date 2024/11/24
 */
@Data
public class WSBaseResp<T> {
    /**
     * ws 服务端推送给前端的消息类型
     * @see com.ershi.hichat.common.user.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    /**
     * 根据不同的消息 type 规定不同的返回对象类型
     * @see com.ershi.hichat.common.user.domain.vo.response.ws.dataclass
     */
    private T data;


}
