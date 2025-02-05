package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import com.ershi.hichat.common.chat.mapper.RoomGroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    /**
     * 根据房间id列表查询群聊房间
     * @param roomIds
     * @return {@link List }<{@link RoomGroup }>
     */
    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIds)
                .list();
    }

    /**
     * 根据房间id查询房间信息
     * @param roomId
     * @return {@link RoomGroup }
     */
    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomGroup::getRoomId, roomId)
                .one();
    }
}
