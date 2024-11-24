package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * websocket 前端请求类型枚举
 *
 * @author Ershi
 * @date 2024/11/24
 */
@AllArgsConstructor
@Getter
public enum WSReqTypeEnum {

    LOGIN(1, "请求登录二维码"),
    HEARTBEAT(2, "心跳包"),
    AUTHORIZE(3, "登录认证"),
    ;

    /**
     * 请求类型
     */
    private Integer type;
    /**
     * 请求描述
     */
    private String desc;


    /**
     * 枚举类型缓存
     */
    private static Map<Integer, WSReqTypeEnum> cache;

    static {
        cache = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     * @param type
     * @return {@link WSReqTypeEnum}
     */
    public static WSReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
