package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.entity.UserFriend;
import com.ershi.hichat.common.user.mapper.UserFriendMapper;
import com.ershi.hichat.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-24
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    /**
     * 获取好友游标分页数据
     * @param uid
     * @param cursorPageBaseReq
     * @return {@link CursorPageBaseResp}<{@link UserFriend}>
     */
    public CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this,
                cursorPageBaseReq,
                // 查询指定uid用户的好友
                wrapper -> wrapper.eq(UserFriend::getUid, uid),
                UserFriend::getId);
    }
}
