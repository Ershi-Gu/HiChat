package com.ershi.hichat.common.common.utils.discover;

import com.ershi.hichat.common.common.utils.discover.domain.UrlAnalysisInfo;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Url解析接口，用于定义子类解析用到的方法规范
 * @author Ershi
 * @date 2025/02/10
 */
public interface UrlDiscover {


    /**
     * 解析url，获取urlInfo；支持批量解析
     * @param content 输入的文本
     * @return {@link Map }<{@link String }, {@link UrlAnalysisInfo }>
     */
    @Nullable
    Map<String, UrlAnalysisInfo> getUrlInfoMap(String content);

    /**
     * 解析url获取urlInfo
     * @param url
     * @return {@link UrlAnalysisInfo }
     */
    @Nullable
    UrlAnalysisInfo getUrlInfo(String url);

    /**
     * 获取网站标题
     * @param document
     * @return {@link String }
     */
    @Nullable
    String getTitle(Document document);

    /**
     * 获取网站简述
     * @param document
     * @return {@link String }
     */
    @Nullable
    String getDescription(Document document);

    /**
     * 获取网站图片
     * @param url
     * @param document
     * @return {@link String }
     */
    @Nullable
    String getImage(String url, Document document);
}
