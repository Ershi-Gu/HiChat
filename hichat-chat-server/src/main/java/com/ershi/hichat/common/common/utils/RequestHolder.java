package com.ershi.hichat.common.common.utils;

import com.ershi.hichat.common.common.domain.dto.RequestInfo;

/**
 * 持有web请求信息上下文
 *
 * @author Ershi
 * @date 2024/12/04
 */
public class RequestHolder {

    private static final ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        threadLocal.set(requestInfo);
    }

    public static RequestInfo get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
