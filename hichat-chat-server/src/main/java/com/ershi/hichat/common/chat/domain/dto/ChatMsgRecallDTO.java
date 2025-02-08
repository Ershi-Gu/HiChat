package com.ershi.hichat.common.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息撤回的推送类
 * @author Ershi
 * @date 2025/02/08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRecallDTO {

    /**
     * 原消息id
     */
    private Long msgId;
    /**
     * 消息所在房间id
     */
    private Long roomId;
    /**
     * 撤回的用户
     */
    private Long recallUid;
}
