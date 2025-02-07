package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.event.UserOfflineEvent;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.IpForUserService;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import com.ershi.hichat.common.websocket.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 用户下线事件监听器
 *
 * @author Ershi
 * @date 2025/02/06
 */
@Slf4j
@Component
public class UserOfflineListener {
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IpForUserService ipForUserService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserInfoCache userInfoCache;

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updateUserLineAndPush(UserOfflineEvent event) {
        User user = event.getUser();
        // 更新用户在线表-redis
        userInfoCache.offline(user.getId(), user.getLastOptTime());
        // todo 推送给所有在线用户，该用户下线
        webSocketService.sendMsgToAllOnline(WSAdapter.buildOfflineNotifyResp(event.getUser()), Collections.singletonList(event.getUser().getId()));
    }

    /**
     * 用户下线后更新相关信息
     * @param event
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updateUserInfoAfterOffline(UserOfflineEvent event) {
        User user = event.getUser();
        userDao.updateById(user);
        // 异步更新用户当前登录Ip详情
        ipForUserService.refreshIpDetailAsync(user.getId());
    }

}
