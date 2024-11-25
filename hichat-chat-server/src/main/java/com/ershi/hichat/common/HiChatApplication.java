package com.ershi.hichat.common;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot 应用启动类
 * @author Ershi
 * @date 2024/11/24
 */
@SpringBootApplication
@MapperScan("com.ershi.hichat.common.**.mapper")
public class HiChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiChatApplication.class);
    }
}
