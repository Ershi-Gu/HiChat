package com.ershi.hichat.common.config;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

/**
 * Ip2Region配置类
 * @author Ershi
 * @date 2024/12/11
 */
@Configuration
public class Ip2RegionConfig {

    /**
     * ip地址数据库文件名，放于resources目录下
     */
    private final String DB_PATH = "ip2region.xdb";

    /**
     * 搜索器
     */
    private Searcher searcher;;

    @Bean
    public Searcher ip2RegionSearcher() {
        try {
            // 1、从 dbPath 加载整个 xdb 到内存。
            ClassPathResource resource = new ClassPathResource(DB_PATH);
            File file = resource.getFile(); // 获取文件对象
            byte[] cBuff = Searcher.loadContentFromFile(file.getAbsolutePath());
            // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            throw new RuntimeException("ip2Region Loading failed:", e);
        }
        return searcher;
    }

    @PreDestroy
    public void closeSearcher() {
        try {
            searcher.close();
        } catch (IOException e) {
            throw new RuntimeException("ip2Region searcher close failed", e);
        }
    }
}
