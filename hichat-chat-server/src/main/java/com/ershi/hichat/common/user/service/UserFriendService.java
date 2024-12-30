package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApplyReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApproveReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendCheckReq;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendCheckResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendUnreadResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-24
 */
public interface UserFriendService {

    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq cursorPageBaseReq);

    FriendCheckResp check(Long uid, FriendCheckReq friendCheckReq);

    void apply(Long uid, FriendApplyReq friendApplyReq);

    void applyApprove(Long uid, FriendApproveReq friendApproveReq);

    FriendUnreadResp unread(Long uid);

    void deleteFriend(Long uid, Long targetUid);
}
