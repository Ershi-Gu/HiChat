package com.ershi.hichat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.chat.domain.entity.Contact;
import com.ershi.hichat.common.chat.mapper.ContactMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    /**
     * 获取用户在指定房间最后一条读取到的消息
     *
     * @param roomId
     * @param receiveUid
     * @return {@link Contact }
     */
    public Contact get(Long roomId, Long receiveUid) {
        return lambdaQuery()
                .eq(Contact::getRoomId, roomId)
                .eq(Contact::getUid, receiveUid)
                .one();
    }
}
