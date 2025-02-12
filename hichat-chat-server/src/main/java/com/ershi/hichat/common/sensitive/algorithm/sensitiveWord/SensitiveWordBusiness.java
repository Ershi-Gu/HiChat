package com.ershi.hichat.common.sensitive.algorithm.sensitiveWord;

import com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.ac.ACTrie;
import com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.fliter.ACFilter;
import com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.fliter.DFAFilter;

import java.util.List;

/**
 * 敏感词功能支持类
 *
 * @author zhaoyuhang
 * @date 2023/07/08
 */
public class SensitiveWordBusiness {

    /**
     * 私有化构造器
     */
    private SensitiveWordBusiness() {
    }

    /**
     * 脱敏策略-默认DFA
     */
    private SensitiveWordFilter sensitiveWordFilter = DFAFilter.getInstance();

    /**
     * 敏感词列表
     */
    private SensitiveWordFactory wordDeny;

    /**
     * 返回敏感词工具类实例
     * @return {@link SensitiveWordBusiness }
     */
    public static SensitiveWordBusiness newInstance() {
        return new SensitiveWordBusiness();
    }

    /**
     * 初始化
     * <p>
     * 1. 根据配置，初始化对应的 map。比较消耗性能。
     *
     * @return this
     * @since 0.0.13
     */
    public SensitiveWordBusiness init() {
        List<String> words = wordDeny.getWordList();
        loadWord(words);
        return this;
    }

    /**
     * 过滤策略
     *
     * @param filter 过滤器
     * @return 结果
     * @since 0.7.0
     */
    public SensitiveWordBusiness filterStrategy(SensitiveWordFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter can not be null");
        }
        this.sensitiveWordFilter = filter;
        return this;
    }

    /**
     * 配置敏感词装配工厂
     * @param wordFactory
     * @return {@link SensitiveWordBusiness }
     */
    public SensitiveWordBusiness sensitiveWord(SensitiveWordFactory wordFactory) {
        if (wordFactory == null) {
            throw new IllegalArgumentException("wordFactory can not be null");
        }
        this.wordDeny = wordFactory;
        return this;
    }

    /**
     * 有敏感词
     *
     * @param text 文本
     * @return boolean
     */
    public boolean hasSensitiveWord(String text) {
        return sensitiveWordFilter.hasSensitiveWord(text);
    }

    /**
     * 过滤
     *
     * @param text 文本
     * @return {@link String}
     */
    public String filter(String text) {
        return sensitiveWordFilter.filter(text);
    }

    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    private void loadWord(List<String> words) {
        sensitiveWordFilter.loadWord(words);
    }

}
