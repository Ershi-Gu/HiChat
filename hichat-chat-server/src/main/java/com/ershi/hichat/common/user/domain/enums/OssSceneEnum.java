package com.ershi.hichat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件上传场景
 * @author Ershi
 * @date 2025/02/12
 */
@AllArgsConstructor
@Getter
public enum OssSceneEnum {
    CHAT_IMG(1, "聊天", "/chatImg"),
    EMOJI(2, "表情包", "/emoji"),
    ;

    private final Integer type;
    private final String desc;
    private final String path;

    private static final Map<Integer, OssSceneEnum> cache;

    static {
        cache = Arrays.stream(OssSceneEnum.values()).collect(Collectors.toMap(OssSceneEnum::getType, Function.identity()));
    }

    public static OssSceneEnum of(Integer type) {
        return cache.get(type);
    }
}
