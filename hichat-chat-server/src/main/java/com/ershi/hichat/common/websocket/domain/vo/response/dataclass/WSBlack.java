package com.ershi.hichat.common.websocket.domain.vo.response.dataclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拉黑用户
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSBlack {
    /**
     * 拉黑用户id
     */
    private Long uid;
}
