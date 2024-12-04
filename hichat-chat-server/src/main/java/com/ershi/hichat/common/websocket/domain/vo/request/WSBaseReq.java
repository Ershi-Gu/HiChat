package com.ershi.hichat.common.websocket.domain.vo.request;

import com.ershi.hichat.common.user.domain.enums.WSReqTypeEnum;
import lombok.Data;

/**
 * websocket 前端基本请求体
 * @author Ershi
 * @date 2024/11/24
 */
@Data
public class WSBaseReq {
    /**
     * 请求类型：
     * @see WSReqTypeEnum
     */
    private Integer type;
    /**
     * 请求包数据
     */
    private String data;
}
