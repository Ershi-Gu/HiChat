package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.event.UserOnlineEvent;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.IpForUserService;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import com.ershi.hichat.common.websocket.service.PushService;
import com.ershi.hichat.common.websocket.service.adapter.WSMsgAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * 用户上线事件监听者
 * @author Ershi
 * @date 2024/12/12
 */
@Component
public class UserOnlineListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IpForUserService ipForUserService;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private PushService pushService;

    /**
     * @param event
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void updateUserLineAndPush(@NotNull UserOnlineEvent event) {
        User user = event.getUser();
        userInfoCache.online(user.getId(), user.getLastOptTime());
        // todo 推送给所有在线用户，该用户登录成功
        pushService.sendPushMsg(WSMsgAdapter.buildOnlineNotifyResp(event.getUser()));
    }

    /**
     * 用户上线后更新online有关信息
     * @param event
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void updateUserInfoAfterOnline(UserOnlineEvent event) {
        User user = event.getUser();
        // 先将最新在线信息更新到DB，避免其他地方需要用到最新数据
        userDao.updateById(user);
        // 异步更新用户当前登录Ip详情
        ipForUserService.refreshIpDetailAsync(user.getId());
    }
}
