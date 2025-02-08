package com.ershi.hichat.common.websocket.domain.enums;

import com.ershi.hichat.common.websocket.domain.vo.response.dataclass.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * websocket 后端推送类型枚举
 *
 * @author Ershi
 * @date 2024/11/24
 */
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {

    LOGIN_URL(1, "登录二维码返回", WSLoginUrl.class),
    LOGIN_SCAN_SUCCESS(2, "用户扫描成功等待授权", null),
    LOGIN_SUCCESS(3, "用户登录成功返回用户信息", WSLoginSuccess.class),
    MESSAGE(4, "新消息", WSMessage.class),
    ONLINE_OFFLINE_NOTIFY(5, "上下线通知", WSOnlineOfflineNotify.class),
    INVALIDATE_TOKEN(6, "使前端的token失效，意味着前端需要重新登录", null),
    BLACK(7, "拉黑用户", WSBlack.class),
    MARK(8, "消息标记", WSMsgMark.class),
    RECALL(9, "消息撤回", WSRecallMsg.class),
    APPLY(10, "好友申请", WSFriendApply.class),
    ;

    /**
     * 推送消息类型
     */
    private final Integer type;
    /**
     * 推送消息描述
     */
    private final String desc;
    /**
     * 推送的数据，不同的消息类型有不同的数据格式
     * @see com.ershi.hichat.common.websocket.domain.vo.response.dataclass
     */
    private final Class dataClass;

    /**
     * 枚举类缓存
     */
    private static Map<Integer, WSRespTypeEnum> cache;

    static {
        cache = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
