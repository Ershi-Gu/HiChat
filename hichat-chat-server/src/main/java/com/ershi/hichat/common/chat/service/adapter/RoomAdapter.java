package com.ershi.hichat.common.chat.service.adapter;

import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.enums.HotFlagEnum;
import com.ershi.hichat.common.chat.domain.enums.RoomStatusEnum;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

public class RoomAdapter {

    /**
     * 单聊房间唯一键间隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 生成单聊房间唯一键，uid1,uid2。其中uid1为小
     * @param uidList
     * @return {@link String}
     */
    public static String generateRoomKey(List<Long> uidList) {
        return uidList.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    /**
     * 构建单聊房间信息
     * @param roomId
     * @param uidList
     * @return {@link RoomFriend}
     */
    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setStatus(RoomStatusEnum.NORMAL.getStatus());
        return roomFriend;
    }

    /**
     * 创建房间（通用）
     * @param typeEnum
     * @return {@link Room}
     */
    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }
}
