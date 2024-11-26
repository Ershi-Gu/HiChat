package com.ershi.hichat.common.websocket.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录和前端ws channel连接的相关信息
 * @author Ershi
 * @date 2024/11/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSChannelExtraDTO {
    /**
     * 前端如果登录了，记录uid关联到channel
     */
    private Long uid;
}
