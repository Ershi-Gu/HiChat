package com.ershi.hichat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 房间状态枚举
 * @author Ershi
 * @date 2024/12/30
 */
@AllArgsConstructor
@Getter
public enum RoomStatusEnum {
    NORMAL(0, "正常"),
    BAN(1, "禁止"),
    ;


    private final Integer status;
    private final String desc;

    private static final Map<Integer, RoomStatusEnum> cache;


    static {
        cache = Arrays.stream(RoomStatusEnum.values()).collect(Collectors.toMap(RoomStatusEnum::getStatus, Function.identity()));
    }

    public static RoomStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
