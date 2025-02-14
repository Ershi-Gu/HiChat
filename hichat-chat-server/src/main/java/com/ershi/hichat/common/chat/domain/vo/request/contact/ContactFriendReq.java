package com.ershi.hichat.common.chat.domain.vo.request.contact;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 查询好友会话请求
 * @author Ershi
 * @date 2025/02/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactFriendReq {

    @NotNull
    @ApiModelProperty("好友uid")
    private Long uid;
}