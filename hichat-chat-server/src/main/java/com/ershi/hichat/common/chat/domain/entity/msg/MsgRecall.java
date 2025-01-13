package com.ershi.hichat.common.chat.domain.entity.msg;

import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息撤回详情
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgRecall implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 1L;
    //撤回消息的uid
    private Long recallUid;
    //撤回的时间点
    private Date recallTime;
}
