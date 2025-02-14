package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.enums.RoomStatusEnum;
import com.ershi.hichat.common.chat.mapper.RoomFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    /**
     * 通过roomId获取房间数据
     * @param roomId
     * @return {@link RoomFriend }
     */
    public RoomFriend getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomId, roomId)
                .one();
    }

    /**
     * 通过单聊房间唯一键获取房间数据
     * @param key
     * @return {@link RoomFriend}
     */
    public RoomFriend getByKey(String key) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomKey, key)
                .one();
    }

    /**
     * 重新启用房间
     * @param id 单聊房间id
     */
    public void restoreRoom(Long id) {
        lambdaUpdate()
                .eq(RoomFriend::getId, id)
                .set(RoomFriend::getStatus, RoomStatusEnum.NORMAL.getStatus())
                .update();
    }

    /**
     * 禁用房间（用于删除好友）
     * @param key 房间唯一键
     */
    public void disableRoom(String key) {
        lambdaUpdate()
                .eq(RoomFriend::getRoomKey, key)
                .set(RoomFriend::getStatus, RoomStatusEnum.BAN)
                .update();
    }

    /**
     * 获取好友房间信息
     * @param roomIds
     * @return {@link List }<{@link RoomFriend }>
     */
    public List<RoomFriend> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomFriend::getRoomId, roomIds)
                .list();
    }
}
