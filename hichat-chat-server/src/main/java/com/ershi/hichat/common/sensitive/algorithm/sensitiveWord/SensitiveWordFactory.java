package com.ershi.hichat.common.sensitive.algorithm.sensitiveWord;

import java.util.List;

/**
 * 敏感词加载工厂，调用方自己实现
 *
 * @author zhaoyuhang
 * @date 2023/07/09
 */
public interface SensitiveWordFactory {

    /**
     * 返回敏感词数据源
     *
     * @return 结果
     * @since 0.0.13
     */
    List<String> getWordList();
}
