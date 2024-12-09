package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 幂等号源渠道类型
 * @author Ershi
 * @date 2024/12/07
 */
@AllArgsConstructor
@Getter
public enum IdempotentSourceEnum {

    UID(1, "uid"),
    MSG_ID(2, "消息id"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, IdempotentSourceEnum> cache;

    static {
        cache = Arrays.stream(IdempotentSourceEnum.values()).collect(Collectors.toMap(IdempotentSourceEnum::getType, Function.identity()));
    }

    public static IdempotentSourceEnum of(Integer type) {
        return cache.get(type);
    }
}
