package com.ershi.hichat.common.chat.dao;

import com.ershi.hichat.common.chat.domain.entity.GroupMember;
import com.ershi.hichat.common.chat.mapper.GroupMemberMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-30
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    /**
     * 获取群成员
     *
     * @param roomId
     * @param uid
     * @return {@link GroupMember }
     */
    public GroupMember getMember(Long roomId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, roomId)
                .eq(GroupMember::getUid, uid)
                .one();
    }
}
