package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 申请类型枚举
 * @author Ershi
 * @date 2024/12/30
 */
@Getter
@AllArgsConstructor
public enum ApplyTypeEnum {

    ADD_FRIEND(1, "加好友"),
    ADD_GROUP(2, "加群聊"),
    ;


    private final Integer type;

    private final String desc;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
