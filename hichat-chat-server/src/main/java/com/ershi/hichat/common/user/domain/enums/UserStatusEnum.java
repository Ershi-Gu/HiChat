package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户帐号状态枚举类
 *
 * @author Ershi
 * @date 2024/12/16
 */
@AllArgsConstructor
@Getter
public enum UserStatusEnum {
    NORMAL(0, "正常账号"),
    BAN(1, "账号已封禁"),
    ;


    private final Integer type;
    private final String desc;

    private static final Map<Integer, UseStatusEnum> cache;


    static {
        cache = Arrays.stream(UseStatusEnum.values()).collect(Collectors.toMap(UseStatusEnum::getStatus, Function.identity()));
    }

    public static UseStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
