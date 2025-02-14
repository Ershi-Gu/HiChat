package com.ershi.hichat.common.chat.service;

import com.ershi.hichat.common.chat.domain.entity.RoomFriend;

import java.util.List;

/**
 * <p>
 * 单聊房间表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
public interface RoomFriendService {

    default RoomFriend createFriendRoom(List<Long> uidList) {
        return null;
    }

    void disableFriendRoom(List<Long> uidList);

    RoomFriend getFriendRoom(Long uid, Long friendUid);
}
