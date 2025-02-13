package com.ershi.hichat.common.websocket.service.adapter;

import com.ershi.hichat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.ershi.hichat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.ershi.hichat.common.chat.domain.vo.response.msg.ChatMessageResp;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import com.ershi.hichat.common.websocket.domain.vo.response.dataclass.WSFriendApply;
import com.ershi.hichat.common.websocket.domain.vo.response.dataclass.WSMsgMark;
import com.ershi.hichat.common.websocket.domain.vo.response.dataclass.WSRecallMsg;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * ws消息适配器
 *
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class WSMsgAdapter {

    /**
     * 构建ws普通消息推送体
     * @param msgResp
     * @return {@link WSBaseResp }<{@link ChatMessageResp }>
     */
    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp msgResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(msgResp);
        return wsBaseResp;
    }

    /**
     * 构建好友申请推送
     * @param wsFriendApply
     * @return {@link WSBaseResp }<{@link ? }>
     */
    public static WSBaseResp<?> buildUserApplySend(WSFriendApply wsFriendApply) {
        WSBaseResp<WSFriendApply> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.APPLY.getType());
        wsBaseResp.setData(wsFriendApply);
        return wsBaseResp;
    }

    /**
     * 构建用户下线通知
     * @param user
     * @return {@link WSBaseResp }<{@link ? }>
     */
    public static WSBaseResp<?> buildOfflineNotifyResp(User user) {
        return null;
    }

    /**
     * 构建用户上线通知
     * @param user
     * @return {@link WSBaseResp }<{@link ? }>
     */
    public static WSBaseResp<?> buildOnlineNotifyResp(User user) {
        return null;
    }

    /**
     * 构建消息撤回的推送
     *
     * @param chatMsgRecallDTO
     * @return {@link WSBaseResp }<{@link ? }>
     */
    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO chatMsgRecallDTO) {
        WSBaseResp<WSRecallMsg> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSRecallMsg recall = new WSRecallMsg();
        BeanUtils.copyProperties(chatMsgRecallDTO, recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }

    /**
     * 构建消息标记推送体
     *
     * @param chatMessageMarkDTO
     * @param markCount
     * @return {@link WSBaseResp }<{@link ? }>
     */
    public static WSBaseResp<?> buildMsgMarkSend(ChatMessageMarkDTO chatMessageMarkDTO, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
        BeanUtils.copyProperties(chatMessageMarkDTO, item);
        item.setMarkCount(markCount);
        WSBaseResp<WSMsgMark> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MARK.getType());
        WSMsgMark mark = new WSMsgMark();
        mark.setMarkList(Collections.singletonList(item));
        wsBaseResp.setData(mark);
        return wsBaseResp;
    }
}
