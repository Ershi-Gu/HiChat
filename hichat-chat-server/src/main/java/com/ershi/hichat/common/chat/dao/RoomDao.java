package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

    /**
     * 更新房间中的最新消息记录点
     * @param roomId
     * @param msgId
     * @param msgTime
     */
    public void refreshActiveTime(Long roomId, Long msgId, Date msgTime) {
        lambdaUpdate()
                .eq(Room::getId, roomId)
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, msgTime)
                .update();
    }
}
