package com.ershi.hichat.common.chat.domain.vo.response.msg;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本消息回复体
 * @author Ershi
 * @date 2025/02/08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgResp implements BaseMsgDTO {
    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("回复的消息，如果没有回复的消息，返回的是null")
    private ReplyMsg reply;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyMsg {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("用户uid")
        private Long uid;
        @ApiModelProperty("用户名称")
        private String username;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容不同的消息类型，见父消息内容体")
        private Object body;
        @ApiModelProperty("是否可消息跳转 0否 1是")
        private Integer canCallback;
        @ApiModelProperty("跳转间隔的消息条数")
        private Integer gapCount;
    }
}
