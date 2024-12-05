package com.ershi.hichat.common.common.exception;

import lombok.Data;

/**
 * 自定义业务异常
 *
 * @author Ershi
 * @date 2024/12/05
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 业务异常码
     */
    private Integer errorCode;
    /**
     * 业务异常信息
     */
    private String errorMsg;

    public BusinessException(String errorMsg) {
        super(errorMsg);
        this.errorCode = BusinessExceptionEnum.BUSINESS_ERROR.getErrorCode();
        this.errorMsg = errorMsg;
    }

    public BusinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
