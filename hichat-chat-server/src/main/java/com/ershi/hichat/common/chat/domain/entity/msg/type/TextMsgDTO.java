package com.ershi.hichat.common.chat.domain.entity.msg.type;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文本消息参数
 * @author Ershi
 * @date 2025/01/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 3598080947382565713L;

    @ApiModelProperty("文本消息内容")
    @NotBlank
    private String content;

    @ApiModelProperty("回复的消息id，没有就不传")
    private Long replyMsgId;

    @ApiModelProperty("艾特的uid，如果是全体艾特传 0")
    private Long[] atUidList;
}
