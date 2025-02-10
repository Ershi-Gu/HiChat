package com.ershi.hichat.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * url解析工厂，用于设置url解析的优先级并链式执行url解析器
 */
public class PrioritizedUrlDiscover extends AbstractUrlDiscover {

    /**
     * 保存url解析器实例
     */
    private final List<UrlDiscover> urlDiscovers = new ArrayList<>(2);

    /**
     * 当初始化url解析器时加入url解析器实现，并设置优先级
     */
    public PrioritizedUrlDiscover() {
        urlDiscovers.add(new WxUrlDiscover());
        urlDiscovers.add(new CommonUrlDiscover());
    }


    @Nullable
    @Override
    public String getTitle(Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlTitle = urlDiscover.getTitle(document);
            if (StrUtil.isNotBlank(urlTitle)) {
                return urlTitle;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getDescription(Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlDescription = urlDiscover.getDescription(document);
            if (StrUtil.isNotBlank(urlDescription)) {
                return urlDescription;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getImage(String url, Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlImage = urlDiscover.getImage(url, document);
            if (StrUtil.isNotBlank(urlImage)) {
                return urlImage;
            }
        }
        return null;
    }
}
