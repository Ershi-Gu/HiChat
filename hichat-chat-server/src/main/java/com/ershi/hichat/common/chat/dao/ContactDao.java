package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.Contact;
import com.ershi.hichat.common.chat.mapper.ContactMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
