package com.ershi.hichat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 消息标记枚举
 *
 * @author Ershi
 * @date 2025/02/13
 */
@AllArgsConstructor
@Getter
public enum MessageMarkTypeEnum {

    LIKE(1, "点赞", 10),
    DISLIKE(2, "点踩", 5),
    ;

    private final Integer type;
    private final String desc;
    private final Integer riseNum; // 当用户某个标记达到一定次数，升级业务，发放对应徽章

    private static final Map<Integer, MessageMarkTypeEnum> cache;

    static {
        cache = Arrays.stream(MessageMarkTypeEnum.values()).collect(Collectors.toMap(MessageMarkTypeEnum::getType, Function.identity()));
    }

    public static MessageMarkTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
