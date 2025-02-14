package com.ershi.hichat.common.chat.service.adapter;


import com.ershi.hichat.common.chat.domain.dto.RoomBaseInfo;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;
import com.ershi.hichat.common.chat.domain.vo.response.contact.ChatContactResp;
import com.ershi.hichat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.user.domain.entity.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会话房间详情构造器
 * @author Ershi
 * @date 2025/02/14
 */
public class RoomContactAdapter {


    /**
     * 从单聊会话中获取好友uid列表
     * @param values
     * @param uid
     * @return {@link Set }<{@link Long }>
     */
    public static Set<Long> getFriendUidSet(Collection<RoomFriend> values, Long uid) {
        return values.stream()
                .map(a -> getFriendUid(a, uid))
                .collect(Collectors.toSet());
    }

    /**
     * 从单聊会话中获取好友uid
     */
    public static Long getFriendUid(RoomFriend roomFriend, Long uid) {
        return Objects.equals(uid, roomFriend.getUid1()) ? roomFriend.getUid2() : roomFriend.getUid1();
    }

    /**
     * 组装会话房间基本信息
     * @param roomMap
     * @param groupRoomMap
     * @param friendRoomMap
     * @return {@link Map }<{@link Long }, {@link RoomBaseInfo }>
     */
    public static Map<Long, RoomBaseInfo> buildRoomBaseInfo(Map<Long, Room> roomMap,
                                                            Map<Long, RoomGroup> groupRoomMap,
                                                            Map<Long, User> friendRoomMap) {
        return roomMap.values().stream()
                .map(room -> {
                    RoomBaseInfo roomBaseInfo = RoomBaseInfo.builder()
                            .roomId(room.getId())
                            .type(room.getType())
                            .hotFlag(room.getHotFlag())
                            .activeTime(room.getActiveTime())
                            .lastMsgId(room.getLastMsgId())
                            .build();
                    // 根据房间类型进行特殊处理
                    if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                        RoomGroup roomGroup = groupRoomMap.get(room.getId());
                        roomBaseInfo.setName(roomGroup.getName());
                        roomBaseInfo.setAvatar(roomGroup.getAvatar());
                    } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                        // 单聊房间显示对方的信息
                        User user = friendRoomMap.get(room.getId());
                        roomBaseInfo.setName(user.getName());
                        roomBaseInfo.setAvatar(user.getAvatar());
                    }
                    return roomBaseInfo;
                }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    /**
     * 构建用户会话列表返回-根据房间最后活跃时间排序
     *
     * @param roomBaseInfoMap
     * @param lastMessages
     * @param lastMsgSendUserInfoMap
     * @param unReadCountMap
     * @return {@link List }<{@link ChatContactResp }>
     */
    public static List<ChatContactResp> buildChatContactResp(Map<Long, RoomBaseInfo> roomBaseInfoMap,
                                                             List<Message> lastMessages,
                                                             Map<Long, User> lastMsgSendUserInfoMap,
                                                             Map<Long, Integer> unReadCountMap) {
        Map<Long, Message> messageMap = lastMessages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        return roomBaseInfoMap.values().stream().map(room -> {
                    ChatContactResp resp = new ChatContactResp();
                    // 组装基本信息
                    RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(room.getRoomId());
                    resp.setAvatar(roomBaseInfo.getAvatar());
                    resp.setRoomId(room.getRoomId());
                    resp.setActiveTime(room.getActiveTime());
                    resp.setHot_Flag(roomBaseInfo.getHotFlag());
                    resp.setType(roomBaseInfo.getType());
                    resp.setName(roomBaseInfo.getName());
                    // 组装最后消息详情
                    Message message = messageMap.get(room.getLastMsgId());
                    if (Objects.nonNull(message)) {
                        // 获取消息返回体
                        AbstractMsgHandler msgHandler = MsgHandlerFactory.getMsgHandlerNoNull(message.getType());
                        resp.setText(lastMsgSendUserInfoMap.get(message.getFromUid()).getName() + ":" + msgHandler.showMsgOnContact(message));
                    }
                    resp.setUnreadCount(unReadCountMap.getOrDefault(room.getRoomId(), 0));
                    return resp;
                })
                // 按照房间最后活跃时间排序，最新的在最上面
                .sorted(Comparator.comparing(ChatContactResp::getActiveTime).reversed())
                .collect(Collectors.toList());
    }
}
