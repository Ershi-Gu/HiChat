package com.ershi.hichat.common.common.exception;

import lombok.AllArgsConstructor;

/**
 * 业务异常类型枚举
 *
 * @author Ershi
 * @date 2024/12/05
 */
@AllArgsConstructor
public enum BusinessErrorEnum implements ErrorEnum{

    BUSINESS_ERROR(1001, "{0}"),
    IP_ANALYSIS_ERROR(1002, "ip解析失败");
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
