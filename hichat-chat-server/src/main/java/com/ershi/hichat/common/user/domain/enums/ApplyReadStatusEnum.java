package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 申请阅读状态枚举
 *
 * @author Ershi
 * @date 2024/12/30
 */
@Getter
@AllArgsConstructor
public enum ApplyReadStatusEnum {

    UNREAD(1, "未读"),

    READ(2, "已读");

    private final Integer status;

    private final String desc;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
