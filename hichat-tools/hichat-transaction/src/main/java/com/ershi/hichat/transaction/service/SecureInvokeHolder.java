package com.ershi.hichat.transaction.service;

import java.util.Objects;

/**
 * 该工具类持有当前正在进行安全调用的方法线程，避免同步调用导致的bug
 * @author Ershi
 * @date 2025/02/06
 */
public class SecureInvokeHolder {
    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();

    public static boolean isInvoking() {
        return Objects.nonNull(INVOKE_THREAD_LOCAL.get());
    }

    public static void setInvoking() {
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }

    public static void invoked() {
        INVOKE_THREAD_LOCAL.remove();
    }
}
