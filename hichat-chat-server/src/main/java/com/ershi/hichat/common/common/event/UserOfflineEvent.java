package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户下线事件
 * @author Ershi
 * @date 2025/02/06
 */
@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private final User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
