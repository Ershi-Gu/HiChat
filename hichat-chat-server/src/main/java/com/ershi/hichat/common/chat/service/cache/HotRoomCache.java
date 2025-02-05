package com.ershi.hichat.common.chat.service.cache;

import com.ershi.hichat.common.common.constant.RedisKey;
import com.ershi.hichat.common.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 热点群聊缓存
 *
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class HotRoomCache {

    /**
     * 更新指定热门群聊房间在记录表中的最新会话时间点-用于热门群聊会话排序
     *
     * @param roomId
     * @param refreshTime
     */
    public void refreshActiveTime(Long roomId, Date refreshTime) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) refreshTime.getTime());
    }
}
