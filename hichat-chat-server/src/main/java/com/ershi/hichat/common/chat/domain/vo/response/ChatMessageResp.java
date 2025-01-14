package com.ershi.hichat.common.chat.domain.vo.response;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 消息返回体
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp {

    @ApiModelProperty("发送者信息")
    private UserInfo fromUser;
    @ApiModelProperty("消息详情")
    private MessageInfo messageInfo;

    /**
     * 发送用户信息
     * @author Ershi
     * @date 2025/01/14
     */
    @Data
    public static class UserInfo {
        @ApiModelProperty("用户id")
        private Long uid;
    }

    /**
     * 返回消息详情
     * @author Ershi
     * @date 2025/01/14
     */
    @Data
    public static class MessageInfo {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("房间id")
        private Long roomId;
        @ApiModelProperty("消息发送时间")
        private Date sendTime;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        /**
         * @see com.ershi.hichat.common.chat.domain.entity.msg.type
         */
        @ApiModelProperty("消息内容不同的消息类型，内容体不同")
        private BaseMsgDTO messageBody;
        @ApiModelProperty("消息标记")
        private MessageMark messageMark;
    }

    /**
     * 消息标记
     * @author Ershi
     * @date 2025/01/14
     */
    @Data
    public static class MessageMark {
        @ApiModelProperty("点赞数")
        private Integer likeCount;
        @ApiModelProperty("该用户是否已经点赞 0否 1是")
        private Integer userLike;
        @ApiModelProperty("举报数")
        private Integer dislikeCount;
        @ApiModelProperty("该用户是否已经举报 0否 1是")
        private Integer userDislike;
    }
}
