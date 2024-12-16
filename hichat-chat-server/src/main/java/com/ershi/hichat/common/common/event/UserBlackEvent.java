package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户封禁事件
 * @author Ershi
 * @date 2024/12/16
 */
@Getter
public class UserBlackEvent extends ApplicationEvent {
    private final User user;

    public UserBlackEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
