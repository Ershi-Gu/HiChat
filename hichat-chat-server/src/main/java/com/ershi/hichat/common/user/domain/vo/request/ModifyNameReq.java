package com.ershi.hichat.common.user.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyNameReq {

    @NotNull
    @Length(max = 6, message = "用户名不能超过6位噢")
    @ApiModelProperty("目标修改的用户名")
    private String name;
}
