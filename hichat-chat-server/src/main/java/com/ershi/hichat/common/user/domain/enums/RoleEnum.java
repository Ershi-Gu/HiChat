package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户身份枚举
 * @author Ershi
 * @date 2024/12/15
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1L, "超级管理员"),
    CHAT_MANAGER(2L, "聊天管理员"),
    ;

    private final Long id;
    private final String desc;

    private static Map<Long, RoleEnum> cache;

    static {
        cache = Arrays.stream(RoleEnum.values()).collect(Collectors.toMap(RoleEnum::getId, Function.identity()));
    }

    public static RoleEnum of(Long type) {
        return cache.get(type);
    }
}
