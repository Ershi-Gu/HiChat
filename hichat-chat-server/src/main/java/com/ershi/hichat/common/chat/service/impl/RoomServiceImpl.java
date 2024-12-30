package com.ershi.hichat.common.chat.service.impl;

import com.ershi.hichat.common.chat.dao.RoomDao;
import com.ershi.hichat.common.chat.dao.RoomFriendDao;
import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.enums.RoomStatusEnum;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;
import com.ershi.hichat.common.chat.service.RoomService;
import com.ershi.hichat.common.chat.service.adapter.RoomAdapter;
import com.ershi.hichat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 房间服务
 * @author Ershi
 * @date 2024/12/30
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private RoomDao roomDao;


    /**
     * 创建房间
     * @param typeEnum
     * @return {@link Room}
     */
    public Room createRoom(RoomTypeEnum typeEnum) {
        Room insert = RoomAdapter.buildRoom(typeEnum);
        roomDao.save(insert);
        return insert;
    }
}
