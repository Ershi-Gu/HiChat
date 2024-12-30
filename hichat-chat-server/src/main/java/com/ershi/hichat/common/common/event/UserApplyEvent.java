package com.ershi.hichat.common.common.event;

import com.ershi.hichat.common.user.domain.entity.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 好友申请事件
 * @author Ershi
 * @date 2024/12/30
 */
@Getter
public class UserApplyEvent extends ApplicationEvent {
    private UserApply userApply;

    public UserApplyEvent(Object source, UserApply userApply) {
        super(source);
        this.userApply = userApply;
    }

}
