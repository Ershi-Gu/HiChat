package com.ershi.hichat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 用户对消息标记状态枚举 <br>
 * Mark-执行 ; UnMark-取消
 *
 * @author Ershi
 * @date 2025/02/13
 */
@AllArgsConstructor
@Getter
public enum MessageMarkStatusEnum {

    DO(1, "执行"),
    UN_DO(2, "取消"),
    ;

    private final Integer type;
    private final String desc;

    private static final Map<Integer, MessageMarkStatusEnum> cache;

    static {
        cache = Arrays.stream(MessageMarkStatusEnum.values()).collect(Collectors.toMap(MessageMarkStatusEnum::getType, Function.identity()));
    }

    public static MessageMarkStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
