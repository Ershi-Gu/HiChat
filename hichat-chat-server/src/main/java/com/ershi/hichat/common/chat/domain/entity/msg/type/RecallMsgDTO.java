package com.ershi.hichat.common.chat.domain.entity.msg.type;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息撤回-当作一条消息来做
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecallMsgDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = -5167216465097409286L;

    /**
     * 撤回消息人的uid
     */
    private Long recallUid;
    /**
     * 撤回的时间点
     */
    private Date recallTime;
}
