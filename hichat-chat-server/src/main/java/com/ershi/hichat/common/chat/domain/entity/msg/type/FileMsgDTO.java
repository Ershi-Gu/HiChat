package com.ershi.hichat.common.chat.domain.entity.msg.type;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseFileDTO;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 语音消息参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 7625826642147680870L;

    @ApiModelProperty("文件名（带后缀）")
    @NotBlank
    private String fileName;

}
