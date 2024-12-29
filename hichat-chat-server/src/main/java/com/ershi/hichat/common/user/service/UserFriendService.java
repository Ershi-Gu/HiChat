package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.vo.response.FriendResp;

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
}
