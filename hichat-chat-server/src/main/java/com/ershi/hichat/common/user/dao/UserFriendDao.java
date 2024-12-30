package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.entity.UserFriend;
import com.ershi.hichat.common.user.mapper.UserFriendMapper;
import com.ershi.hichat.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
     *
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


    /**
     * 从目标uid用户中获取自己好友的信息，若目标列表中包含自己的好友就返回信息，反之返回空列表
     *
     * @param uid     请求用户id
     * @param uidList 目标用户id
     * @return {@link List}<{@link UserFriend}>
     */
    public List<UserFriend> getFriendsInTargetList(Long uid, List<Long> uidList) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .in(UserFriend::getFriendUid, uidList)
                .list();
    }

    /**
     * 获取好友关系记录
     *
     * @param uid
     * @param targetUid
     * @return {@link UserFriend}
     */
    public UserFriend getByFriend(Long uid, Long targetUid) {
        return lambdaQuery().eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .one();
    }

    /**
     * 获取双向好友关系数据
     * @param uid
     * @param targetUid
     * @return {@link List}<{@link UserFriend}>
     */
    public List<UserFriend> getUserFriend(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, targetUid)
                .or()
                .eq(UserFriend::getFriendUid, uid)
                .eq(UserFriend::getUid, targetUid)
                .select(UserFriend::getId)
                .list();
    }
}
