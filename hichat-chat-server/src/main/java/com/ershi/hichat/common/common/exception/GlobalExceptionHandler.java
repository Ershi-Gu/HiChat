package com.ershi.hichat.common.common.exception;

import com.ershi.hichat.common.common.domain.vo.response.ApiResult;
import com.ershi.hichat.common.common.utils.adapter.ErrorMsgAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 *
 * @author Ershi
 * @date 2024/12/05
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * validation 参数校验异常
     *
     * @param e
     * @return {@link ApiResult}<{@link Void}>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String errorMsgStr = ErrorMsgAdapter.buildMethodArgumentNotValidErrorMsg(e);
        log.error("Validation Parameters Error！The reason is: {}", errorMsgStr);
        return ApiResult.fail(SystemCommonErrorEnum.PARAM_VALID.getErrorCode(), errorMsgStr);
    }

    /**
     * 参数为空异常捕获
     * @param e
     * @return {@link ApiResult}<{@link Void}>
     */
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ApiResult<Void> methodArgumentNotBindExceptionHandler(BindException e) {
        log.error("Validation Parameters Error！The reason is: {}", e.getMessage());
        return ApiResult.fail(SystemCommonErrorEnum.PARAM_VALID);
    }

    /**
     * 业务异常处理
     *
     * @param e
     * @return {@link ApiResult}<{@link Void}>
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> businessExceptionHandler(BusinessException e) {
        log.info("Business Exception！The reason is: {}", e.getErrorMsg());
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 未知异常兜底
     *
     * @param e
     * @return {@link ApiResult}<{@link Void}>
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> systemExceptionHandler(Exception e) {
        log.error("System Exception! The reason is: {}", e.getMessage(), e);
        return ApiResult.fail(SystemCommonErrorEnum.SYSTEM_ERROR);
    }

}
