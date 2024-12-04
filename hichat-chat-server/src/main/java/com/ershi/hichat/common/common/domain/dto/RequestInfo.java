package com.ershi.hichat.common.common.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * web请求相关信息
 * @author Ershi
 * @date 2024/12/04
 */
@Data
@Builder
public class RequestInfo {

    /**
     * 用户uid
     */
    private Long uid;
    /**
     * 用户ip
     */
    private String ip;
}
