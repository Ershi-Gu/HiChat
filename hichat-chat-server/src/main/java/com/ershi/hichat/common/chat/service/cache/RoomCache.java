package com.ershi.hichat.common.chat.service.cache;

import com.ershi.hichat.common.cache.AbstractRedisStringCache;
import com.ershi.hichat.common.chat.dao.RoomDao;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.common.constant.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 房间信息缓存-[roomId, Room]
 * @author Ershi
 * @date 2025/01/14
 */
@Service
public class RoomCache extends AbstractRedisStringCache<Long, Room> {

    @Autowired
    private RoomDao roomDao;

    public static final long ROOM_INFO_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_INFO_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIdList) {
        List<Room> rooms = roomDao.listByIds(roomIdList);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
