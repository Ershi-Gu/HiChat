package com.ershi.hichat.common.chat.domain.vo.response.contact;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户会话列表返回体
 * @author Ershi
 * @date 2025/02/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatContactResp implements Serializable {

    private static final long serialVersionUID = 5379841954399922782L;

    @ApiModelProperty("房间id")
    private Long roomId;

    @ApiModelProperty("房间类型 1群聊 2单聊")
    private Integer type;

    @ApiModelProperty("是否热点群聊 -1全员群 0否 1是")
    private Integer hot_Flag;

    @ApiModelProperty("最新消息")
    private String text;

    @ApiModelProperty("会话名称")
    private String name;

    @ApiModelProperty("会话头像")
    private String avatar;

    @ApiModelProperty("房间最后活跃时间(用来排序)")
    private Date activeTime;

    @ApiModelProperty("未读数")
    private Integer unreadCount;
}
