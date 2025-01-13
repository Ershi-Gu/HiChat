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
 * emoji表情参数
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmojisMsgDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("下载地址")
    @NotBlank
    private String url;
}


