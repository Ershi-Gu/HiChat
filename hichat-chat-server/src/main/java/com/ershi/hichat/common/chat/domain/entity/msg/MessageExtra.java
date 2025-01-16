package com.ershi.hichat.common.chat.domain.entity.msg;

import com.ershi.hichat.common.chat.domain.entity.msg.type.*;
import com.ershi.hichat.common.common.domain.UrlInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


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
     * url跳转链接信息
     */
    private Map<String, UrlInfo> urlContentMap;
    /**
     * 消息撤回详情
     */
    private MsgRecallDTO recall;
    /**
     * 文本消息
     */
    private TextMsgDTO textMsg;
    /**
     * 文件消息
     */
    private FileMsgDTO fileMsg;
    /**
     * 图片消息
     */
    private ImgMsgDTO imgMsg;
    /**
     * 语音消息
     */
    private SoundMsgDTO soundMsg;
    /**
     * 文件消息
     */
    private VideoMsgDTO videoMsg;
    /**
     * 表情图片信息
     */
    private EmojisMsgDTO emojisMsg;
}
