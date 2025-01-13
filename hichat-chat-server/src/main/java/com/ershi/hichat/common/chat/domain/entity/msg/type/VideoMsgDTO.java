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
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 视频消息参数
 * @author Ershi
 * @date 2025/01/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("缩略图宽度（像素）")
    @NotNull
    private Integer thumbWidth;

    @ApiModelProperty("缩略图高度（像素）")
    @NotNull
    private Integer thumbHeight;

    @ApiModelProperty("缩略图大小（字节）")
    @NotNull
    private Long thumbSize;

    @ApiModelProperty("缩略图下载地址")
    @NotBlank
    private String thumbUrl;

}
