package com.ershi.hichat.common.common.utils.adapter;

import cn.hutool.extra.servlet.ServletUtil;
import com.ershi.hichat.common.common.domain.dto.RequestInfo;
import com.ershi.hichat.common.common.interceptor.TokenInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 *
 * @author Ershi
 * @date 2024/12/04
 */
public class RequestInfoAdapter {

    public static RequestInfo build(HttpServletRequest request) {
        return RequestInfo.builder()
                .uid(Optional.ofNullable(request.getAttribute(TokenInterceptor.ATTRIBUTE_UID))
                        .map(Object::toString)
                        .map(Long::parseLong)
                        .orElse(null))
                .ip(ServletUtil.getClientIP(request))
                .build();
    }
}
