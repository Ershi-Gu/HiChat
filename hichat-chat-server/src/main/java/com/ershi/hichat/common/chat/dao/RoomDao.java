package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.Room;
import com.ershi.hichat.common.chat.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
