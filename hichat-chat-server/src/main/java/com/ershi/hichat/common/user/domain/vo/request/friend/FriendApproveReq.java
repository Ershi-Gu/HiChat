package com.ershi.hichat.common.user.domain.vo.request.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * 好友申请批准请求
 * @author Ershi
 * @date 2024/12/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApproveReq {

    @NotNull
    @ApiModelProperty("申请id")
    private Long applyId;

}
