package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户在线状态类型枚举
 * @author Ershi
 * @date 2024/11/24
 */
@AllArgsConstructor
@Getter
public enum ChatActiveStatusEnum {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线"),
    ;

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 描述
     */
    private final String desc;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
