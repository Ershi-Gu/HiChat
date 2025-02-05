package com.ershi.hichat.transaction.config;

import com.ershi.hichat.transaction.service.MQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class TransactionAutoConfiguration {

    @Bean
    public MQProducer getMQProducer() {
        return new MQProducer();
    }
}
