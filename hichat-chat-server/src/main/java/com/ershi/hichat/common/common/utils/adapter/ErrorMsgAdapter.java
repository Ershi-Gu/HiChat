package com.ershi.hichat.common.common.utils.adapter;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author Ershi
 * @date 2024/12/05
 */
public class ErrorMsgAdapter {

    /**
     * 构建参数校验异常信息
     *
     * @param e
     * @return {@link String}
     */
    public static String buildMethodArgumentNotValidErrorMsg(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getFieldErrors().forEach(x -> errorMsg.append(x.getField())
                .append("，")
                .append(x.getDefaultMessage())
                .append("，")
        );
        return errorMsg.substring(0, errorMsg.length() - 1);
    }
}
