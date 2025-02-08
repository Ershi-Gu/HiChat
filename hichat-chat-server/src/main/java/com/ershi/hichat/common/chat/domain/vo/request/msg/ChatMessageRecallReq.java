package com.ershi.hichat.common.chat.domain.vo.request.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 撤回消息请求
 * @author Ershi
 * @date 2025/02/07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRecallReq {
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}