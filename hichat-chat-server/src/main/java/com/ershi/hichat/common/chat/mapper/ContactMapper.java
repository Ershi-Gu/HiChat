package com.ershi.hichat.common.chat.mapper;

import com.ershi.hichat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会话列表 Mapper 接口
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
public interface ContactMapper extends BaseMapper<Contact> {

    /**
     * 刷新或创建用户收件箱数据
     * @param roomId 房间id
     * @param memberUidList 用户列表
     * @param msgId 消息id
     * @param activeTime 该房间最后更新时间
     */
    void refreshOrCreateActiveTime(@Param("roomId") Long roomId,
                                   @Param("memberUidList") List<Long> memberUidList,
                                   @Param("msgId") Long msgId,
                                   @Param("activeTime") Date activeTime);
}
