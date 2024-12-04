package com.ershi.hichat.common.common.exception;

/**
 * 顶层异常类规范接口
 * @author Ershi
 * @date 2024/12/04
 */
public interface ErrorEnum {

    Integer getErrorCode();

    String getErrorMsg();
}
