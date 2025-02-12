package com.ershi.hichat.common.common.config;

import com.ershi.hichat.common.sensitive.HiChatWordFactory;
import com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.fliter.DFAFilter;
import com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.SensitiveWordBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 敏感词工具装配
 * @author Ershi
 * @date 2025/02/12
 */
@Configuration
public class SensitiveWordConfig {

    @Autowired
    private HiChatWordFactory hiChatWordFactory;

    /**
     * 初始化引导类
     *
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBusiness sensitiveWordBusiness() {
        return SensitiveWordBusiness.newInstance()
                .filterStrategy(DFAFilter.getInstance())
                .sensitiveWord(hiChatWordFactory)
                .init();
    }

}