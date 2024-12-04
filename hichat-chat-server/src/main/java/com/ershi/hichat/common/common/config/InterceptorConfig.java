package com.ershi.hichat.common.common.config;

import com.ershi.hichat.common.common.interceptor.CollectorInterceptor;
import com.ershi.hichat.common.common.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 *
 * @author Ershi
 * @date 2024/12/04
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private CollectorInterceptor collectorInterceptor;

    @Autowired
    private TokenInterceptor tokenInterceptor;

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/capi/**");
    }
}
