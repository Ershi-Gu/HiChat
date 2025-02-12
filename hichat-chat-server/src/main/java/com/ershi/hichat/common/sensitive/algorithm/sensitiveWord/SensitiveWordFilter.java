package com.ershi.hichat.common.sensitive.algorithm.sensitiveWord;


import java.util.List;

/**
 * 敏感词过滤器模板
 *
 * @author zhaoyuhang
 * @date 2023/07/08
 */
public interface SensitiveWordFilter {

    /**
     * 判断是否有有敏感词
     *
     * @param text 文本
     * @return boolean
     */
    boolean hasSensitiveWord(String text);

    /**
     * 过滤敏感词
     *
     * @param text 文本
     * @return {@link String}
     */
    String filter(String text);

    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    void loadWord(List<String> words);
}
