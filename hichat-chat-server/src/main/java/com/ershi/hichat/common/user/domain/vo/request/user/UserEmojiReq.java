package com.ershi.hichat.common.user.domain.vo.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 表情包请求
 *
 * @author Ershi
 * @date 2025/02/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiReq {

    /**
     * 表情地址
     */
    @ApiModelProperty(value = "新增表情的url")
    private String expressionUrl;

}
