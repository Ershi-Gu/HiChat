package com.ershi.hichat.common.user.domain.vo.request.oss;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 上传文件请求
 * @author Ershi
 * @date 2025/02/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileReq {
    @ApiModelProperty(value = "文件名（带后缀）")
    @NotBlank
    private String fileName;
    @ApiModelProperty(value = "上传场景 1-聊天室图片 2-表情包")
    @NotNull
    private Integer scene;
}
