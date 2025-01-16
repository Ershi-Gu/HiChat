package com.ershi.hichat.common.chat.service.cache;

import com.ershi.hichat.common.cache.AbstractRedisStringCache;
import com.ershi.hichat.common.chat.dao.RoomGroupDao;
import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import com.ershi.hichat.common.common.constant.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 群聊信息缓存
 * @author Ershi
 * @date 2025/01/14
 */
@Service
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {

    @Autowired
    private RoomGroupDao roomGroupDao;

    public static final long ROOM_GROUP_INFO_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_GROUP_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_GROUP_INFO_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> roomIds) {
        List<RoomGroup> roomGroups = roomGroupDao.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomGroup::getRoomId, Function.identity()));
    }
}
