package com.ershi.hichat.common.common.exception;

import lombok.AllArgsConstructor;

/**
 * 业务异常类型枚举
 *
 * @author Ershi
 * @date 2024/12/05
 */
@AllArgsConstructor
public enum BusinessExceptionEnum implements ErrorEnum{

    BUSINESS_ERROR(1001, "通用业务异常"),
    ;

    private Integer errorCode;

    private String errorMsg;

    @Override
    public Integer getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
