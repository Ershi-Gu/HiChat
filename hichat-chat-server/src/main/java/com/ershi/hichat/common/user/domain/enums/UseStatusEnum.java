package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum UseStatusEnum {
    TO_BE_USED(0, "待使用"),
    USED_ALREADY(1, "已使用"),
    ;

    private final Integer status;
    private final String desc;

    private static Map<Integer, UseStatusEnum> cache;

    static {
        cache = Arrays.stream(UseStatusEnum.values()).collect(Collectors.toMap(UseStatusEnum::getStatus, Function.identity()));
    }

    public static UseStatusEnum of(Integer type) {
        return cache.get(type);
    }

    public static Integer toStatus(Boolean bool) {
        return bool ? USED_ALREADY.getStatus() : TO_BE_USED.getStatus();
    }
}
