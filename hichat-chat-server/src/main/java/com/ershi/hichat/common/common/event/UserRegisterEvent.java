package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户注册事件
 * @author Ershi
 * @date 2024/12/10
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {

    /**
     * 用户注册信息
     */
    private final User user;

    /**
     * 构建用户注册事件
     * @param source 事件来源 -> 发送者this
     * @param user 用户注册信息
     */
    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
