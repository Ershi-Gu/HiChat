package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.event.UserApplyEvent;
import com.ershi.hichat.common.user.dao.UserApplyDao;
import com.ershi.hichat.common.user.domain.entity.UserApply;
import com.ershi.hichat.common.websocket.domain.vo.response.dataclass.WSFriendApply;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 好友申请事件监听
 *
 * @author zhongzb create on 2022/08/26
 * @date 2024/12/30
 */
@Slf4j
@Component
public class UserApplyListener {
    @Autowired
    private UserApplyDao userApplyDao;
    @Autowired
    private WebSocketService webSocketService;

    /**
     * 好友申请后，通知目标用户查看
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyTargetFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
        // 获取目标用户所有未读申请的数量通知前端
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        // todo 集群广播申请未读消息
    }

}
