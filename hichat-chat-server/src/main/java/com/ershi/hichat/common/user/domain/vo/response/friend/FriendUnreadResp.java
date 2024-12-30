package com.ershi.hichat.common.user.domain.vo.response.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 未读申请列表返回
 *
 * @author Ershi
 * @date 2024/12/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendUnreadResp {

    @ApiModelProperty("申请列表的未读数")
    private Integer unReadCount;

}
