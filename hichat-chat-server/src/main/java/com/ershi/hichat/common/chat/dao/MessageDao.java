package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.mapper.MessageMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

}
