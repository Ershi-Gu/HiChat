package com.ershi.hichat.common.websocket.domain.vo.response.dataclass;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对消息进行标记
 *
 * @author Ershi
 * @date 2024/11/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMsgMark {

    private List<WSMsgMarkItem> markList;

    @Data
    public static class WSMsgMarkItem {

        @ApiModelProperty("操作者")
        private Long uid;

        @ApiModelProperty("消息id")
        private Long msgId;

        /**
         * @see com.ershi.hichat.common.chat.domain.enums.MessageMarkTypeEnum
         */
        @ApiModelProperty("标记类型 1点赞 2举报")
        private Integer markType;

        @ApiModelProperty("被标记的数量")
        private Integer markCount;

        /**
         * @see com.ershi.hichat.common.chat.domain.enums.MessageMarkActTypeEnum
         */
        @ApiModelProperty("动作类型 1确认 2取消")
        private Integer actType;
    }
}
