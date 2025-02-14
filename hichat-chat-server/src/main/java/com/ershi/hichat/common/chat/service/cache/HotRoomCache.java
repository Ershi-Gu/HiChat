package com.ershi.hichat.common.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.ershi.hichat.common.common.constant.RedisKey;
import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.utils.CursorUtils;
import com.ershi.hichat.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * 热点群聊缓存
 *
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class HotRoomCache {

    /**
     * 获取热门群聊翻页[roomId-active_time]
     *
     * @param pageBaseReq
     * @return
     */
    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.HOT_ROOM_ZET), Long::parseLong);
    }

    /**
     * 获取房间活跃时间在hotRecent和hotOld之间的房间，包含边界
     * @param hotOld
     * @param hotRecent
     * @return {@link Set }<{@link ZSetOperations.TypedTuple }<{@link String }>>
     */
    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotOld, Double hotRecent) {
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), hotOld, hotRecent);
    }

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
