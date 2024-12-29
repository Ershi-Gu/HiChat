package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.dao.UserFriendDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserFriend;
import com.ershi.hichat.common.user.domain.vo.response.FriendResp;
import com.ershi.hichat.common.user.service.UserFriendService;
import com.ershi.hichat.common.user.service.adapter.FriendAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFriendServiceImpl implements UserFriendService {

    @Autowired
    private UserFriendDao userFriendDao;

    @Autowired
    private UserDao userDao;


    /**
     * 获取好友列表的分页返回
     *
     * @param uid 当前用户uid
     * @param cursorPageBaseReq 分页请求信息
     * @return {@link CursorPageBaseResp}<{@link FriendResp}> 返回好友uid列表，具体好友信息由前端懒加载
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        // 获取当前用户的好友表数据
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, cursorPageBaseReq);
        // 判断是否为空
        if (friendPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        // 根据好友表数据获取所有好友的uid
        List<Long> friendUidList = friendPage.getList()
                .stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        // 根据uid获取好友详细用户信息
        List<User> friendUserList = userDao.getFriendList(friendUidList);
        // 将数据转换成响应格式返回
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriendRespList(friendUserList));
    }
}
