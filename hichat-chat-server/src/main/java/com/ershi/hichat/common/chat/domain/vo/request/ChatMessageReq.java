package com.ershi.hichat.common.chat.domain.vo.request;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * 聊天消息请求参数
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReq {
    @NotNull
    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("消息类型")
    @NotNull
    private Integer msgType;

    /**
     * @see com.ershi.hichat.common.chat.domain.entity.msg
     */
    @ApiModelProperty("消息内容，类型不同传值不同")
    @NotNull
    private Object messageBody;

}
