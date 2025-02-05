package com.ershi.hichat.common.chat.service.cache;

import com.ershi.hichat.common.chat.dao.GroupMemberDao;
import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.dao.RoomGroupDao;
import com.ershi.hichat.common.chat.domain.entity.RoomGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.ershi.hichat.common.common.constant.SpringCacheConstant.GROUP_MEMBER_CACHE_NAME;

/**
 * 群成员相关信息缓存
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class GroupMemberCache {

    @Autowired
    private RoomGroupDao roomGroupDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    /**
     * 获取房间成员uid列表
     * @param roomId
     * @return {@link List }<{@link Long }>
     */
    @Cacheable(cacheNames = GROUP_MEMBER_CACHE_NAME, key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }

    @CacheEvict(cacheNames = GROUP_MEMBER_CACHE_NAME, key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }

}
