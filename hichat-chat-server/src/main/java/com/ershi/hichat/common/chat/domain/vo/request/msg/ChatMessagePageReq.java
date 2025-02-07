package com.ershi.hichat.common.chat.domain.vo.request.msg;

import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 获取消息列表请求

 * @author Ershi
 * @date 2025/02/07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessagePageReq extends CursorPageBaseReq {

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}
