package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.event.UserRegisterEvent;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.enums.IdempotentSourceEnum;
import com.ershi.hichat.common.user.domain.enums.ItemEnum;
import com.ershi.hichat.common.user.service.UserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户注册事件监听者
 *
 * @author Ershi
 * @date 2024/12/10
 */
@Component
public class UserRegisterListener {

    @Autowired
    private UserBackpackService userBackpackService;

    /**
     * 给新注册的用户发放一张改名卡
     * @param event 用户注册事件
     */
    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendModifyNameCard(UserRegisterEvent event) {
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(),
                IdempotentSourceEnum.UID, user.getId().toString());
    }
}
