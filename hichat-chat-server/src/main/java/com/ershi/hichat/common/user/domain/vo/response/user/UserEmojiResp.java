package com.ershi.hichat.common.user.domain.vo.response.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表情包返回
 * @author Ershi
 * @date 2025/02/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiResp {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 表情地址
     */
    @ApiModelProperty(value = "表情url")
    private String expressionUrl;

}
