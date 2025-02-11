package com.ershi.hichat.common;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * SpringBoot 应用启动类
 * @author Ershi
 * @date 2024/11/24
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.ershi.hichat.common.**.mapper")
@ServletComponentScan
public class HiChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiChatApplication.class);
    }
}
