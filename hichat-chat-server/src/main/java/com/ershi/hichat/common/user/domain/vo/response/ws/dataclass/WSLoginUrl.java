package com.ershi.hichat.common.user.domain.vo.response.ws.dataclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 登录二维码url推送
 * @author Ershi
 * @date 2024/11/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginUrl {
    /**
     * 二维码 URL
     */
    private String loginUrl;
}
