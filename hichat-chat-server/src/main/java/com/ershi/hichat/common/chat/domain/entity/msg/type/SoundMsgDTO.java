package com.ershi.hichat.common.chat.domain.entity.msg.type;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseFileDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 语音消息参数
 * @author Ershi
 * @date 2025/01/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SoundMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("时长（秒）")
    @NotNull
    private Integer second;
}
