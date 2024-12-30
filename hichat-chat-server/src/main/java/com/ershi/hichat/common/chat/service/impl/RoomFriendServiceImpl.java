package com.ershi.hichat.common.chat.service.impl;

import com.ershi.hichat.common.chat.dao.RoomFriendDao;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.enums.RoomStatusEnum;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;
import com.ershi.hichat.common.chat.service.RoomFriendService;
import com.ershi.hichat.common.chat.service.RoomService;
import com.ershi.hichat.common.chat.service.adapter.RoomAdapter;
import com.ershi.hichat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 单聊房间服务
 * @author Ershi
 * @date 2024/12/30
 */
@Service
public class RoomFriendServiceImpl implements RoomFriendService {

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private RoomService roomService;

    /**
     * 创建一个单聊房间
     * @param uidList
     * @return {@link RoomFriend}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uidList) {
        // 校验参数
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        // 生成房间唯一键
        String friendRoomKey = RoomAdapter.generateRoomKey(uidList);
        // 校验房间是否存在
        RoomFriend roomFriend = roomFriendDao.getByKey(friendRoomKey);
        if (Objects.nonNull(roomFriend)) { //如果存在房间就恢复，适用于恢复好友场景
            restoreRoomFriendIfNeed(roomFriend);
        } else {
            // 新建房间
            Room room = roomService.createRoom(RoomTypeEnum.FRIEND);
            roomFriend = createNewFriendRoom(room.getId(), uidList);
        }
        return roomFriend;
    }

    /**
     * 禁用一个单聊房间（用于删除好友的情况）
     *
     * @param uidList
     */
    @Override
    public void disableFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        String key = RoomAdapter.generateRoomKey(uidList);
        roomFriendDao.disableRoom(key);
    }

    /**
     * 创建单聊房间
     * @param roomId 房间id
     * @param uidList
     * @return {@link RoomFriend}
     */
    private RoomFriend createNewFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend insert = RoomAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(insert);
        return insert;
    }

    /**
     * 还原单聊房间为正常状态，用于以前删除过好友重新启动
     * @param room
     */
    private void restoreRoomFriendIfNeed(RoomFriend roomFriend) {
        if (Objects.equals(roomFriend.getStatus(), RoomStatusEnum.BAN.getStatus())) {
            roomFriendDao.restoreRoom(roomFriend.getId());
        }
    }
}
