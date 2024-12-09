package com.ershi.hichat.common.common.exception;

import lombok.AllArgsConstructor;

/**
 * 系统通用异常枚举
 * @author Ershi
 * @date 2024/12/05
 */
@AllArgsConstructor
public enum SystemCommonErrorEnum implements ErrorEnum {

    SYSTEM_ERROR(-1, "系统出小差了，请稍后再试哦~"),
    PARAM_VALID(-2, "参数校验失败"),
    LOCK_LIMIT(-3, "请求太频繁了，请稍后再试哦"),
    ;

    /**
     * 异常码
     */
    private final Integer code;
    /**
     * 异常信息
     */
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
