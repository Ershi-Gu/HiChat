package com.ershi.hichat.common.chat.domain.entity.msg;

import com.ershi.hichat.common.chat.domain.entity.msg.type.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 消息扩展属性-用于传递msgTypeDTO参数到到Message实体类，保存消息额外信息
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略值为null的参数
public class MessageExtra implements Serializable {

    private static final long serialVersionUID = -8389910228294638090L;

    /**
     * 撤回消息-也是一条消息
     */
    private RecallMsgDTO recallMsgDTO;
    /**
     * 文本消息
     */
    private TextMsgDTO textMsgDTO;
    /**
     * 文件消息
     */
    private FileMsgDTO fileMsgDTO;
    /**
     * 图片消息
     */
    private ImgMsgDTO imgMsgDTO;
    /**
     * 语音消息
     */
    private SoundMsgDTO soundMsgDTO;
    /**
     * 文件消息
     */
    private VideoMsgDTO videoMsgDTO;
    /**
     * 表情图片信息
     */
    private EmojisMsgDTO emojisMsgDTO;
}
