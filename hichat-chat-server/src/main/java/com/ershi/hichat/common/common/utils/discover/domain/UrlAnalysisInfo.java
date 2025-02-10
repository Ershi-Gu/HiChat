package com.ershi.hichat.common.common.utils.discover.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * url 卡片解析后数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlAnalysisInfo {

    /**
     * 标题
     **/
    private String title;
    /**
     * 描述
     **/
    private String description;
    /**
     * 网站LOGO
     **/
    private String image;
}
