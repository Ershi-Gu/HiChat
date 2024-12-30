package com.ershi.hichat.common.chat.service;

import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.domain.entity.RoomFriend;
import com.ershi.hichat.common.chat.domain.enums.RoomTypeEnum;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
public interface RoomService {

    Room createRoom(RoomTypeEnum typeEnum);
}
