package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.event.UserOnlineEvent;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.service.IpForUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
