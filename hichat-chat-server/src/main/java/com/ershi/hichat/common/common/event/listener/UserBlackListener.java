package com.ershi.hichat.common.common.event.listener;

import com.ershi.hichat.common.common.event.UserBlackEvent;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.hichat.common.websocket.domain.vo.response.WSBaseResp;
import com.ershi.hichat.common.websocket.domain.vo.response.dataclass.WSBlack;
import com.ershi.hichat.common.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;

/**
 * 用户封禁事件监听者
 *
 * @author Ershi
 * @date 2024/12/16
 */
@Component
public class UserBlackListener {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserDao userDao;

    /**
     * 向所有在线用户发出拉黑该用户的消息
     *
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent event) {
        // 构建消息体
        User user = event.getUser();
        WSBlack blackUser = WSBlack.builder().uid(user.getId()).build();
        WSBaseResp<WSBlack> wsBlackWSBaseResp = WSBaseResp.build(WSRespTypeEnum.BLACK.getType(), blackUser);
        // 调用websocket服务
        webSocketService.sendMsgToAllOnline(wsBlackWSBaseResp, Collections.singletonList(user.getId()));
    }

    /**
     * 将用户账号状态设置为封禁
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void invalidUserStatus(UserBlackEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }
}
