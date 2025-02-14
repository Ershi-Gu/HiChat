package com.ershi.hichat.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 会话房间基本信息
 *
 * @author Ershi
 * @date 2025/02/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomBaseInfo {

    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("会话名称")
    private String name;

    @ApiModelProperty("会话头像")
    private String avatar;

    @ApiModelProperty("房间类型 1群聊 2单聊")
    private Integer type;

    @ApiModelProperty("是否是热点群 -1全员群 0否 1是")
    private Integer hotFlag;

    @ApiModelProperty("群最后消息的更新时间")
    private Date activeTime;

    @ApiModelProperty("最后一条消息id")
    private Long lastMsgId;
}
