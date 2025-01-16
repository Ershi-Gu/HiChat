package com.ershi.hichat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 消息状态枚举
 * @author Ershi
 * @date 2025/01/14
 */
@AllArgsConstructor
@Getter
public enum MessageStatusEnum {
    NORMAL(0, "正常"),
    DELETE(1, "已删除"),
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
