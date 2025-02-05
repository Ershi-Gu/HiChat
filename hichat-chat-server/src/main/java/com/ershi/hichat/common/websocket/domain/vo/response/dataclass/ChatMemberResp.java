package com.ershi.hichat.common.websocket.domain.vo.response.dataclass;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 群成员列表的成员在线信息
 * @author Ershi
 * @date 2025/02/05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {
    @ApiModelProperty("uid")
    private Long uid;
    /**
     * @see com.ershi.hichat.common.user.domain.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;
    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;
}
