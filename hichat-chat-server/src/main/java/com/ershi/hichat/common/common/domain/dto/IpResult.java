package com.ershi.hichat.common.common.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 请求淘宝ip库解析返回体
 * @author Ershi
 * @date 2024/12/12
 */
@Data
public class IpResult<T> implements Serializable {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("返回消息")
    private String msg;
    @ApiModelProperty("返回数据对象")
    private T data;

    /**
     * 请求是否成功
     * @return boolean
     */
    public boolean isSuccess() {
        return Objects.nonNull(this.code) && this.code == 0;
    }
}
