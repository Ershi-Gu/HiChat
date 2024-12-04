package com.ershi.hichat.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.ershi.hichat.common.common.domain.vo.ApiResult;
import com.google.common.base.Charsets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http业务异常码
 *
 * @author Ershi
 * @date 2024/12/04
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum {

    ACCESS_DENIED(401, "登录失败，请重新登录"),
    ;

    private final Integer httpCode;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return httpCode;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }


    /**
     * 发送当前枚举类对应的http错误信息（JSON格式响应）
     * @param response
     * @throws IOException
     */
    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(this.getErrorCode());
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        ApiResult<Object> responseData = ApiResult.fail(this);
        response.getWriter().write(JSONUtil.toJsonStr(responseData));
    }
}
