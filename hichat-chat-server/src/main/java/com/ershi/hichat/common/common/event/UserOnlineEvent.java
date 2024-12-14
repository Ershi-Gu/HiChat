package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户上线事件
 * @author Ershi
 * @date 2024/12/12
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {

    private final User user;

    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
