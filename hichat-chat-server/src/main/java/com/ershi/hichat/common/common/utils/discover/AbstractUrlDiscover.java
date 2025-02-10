package com.ershi.hichat.common.common.utils.discover;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ershi.hichat.common.common.utils.discover.domain.UrlAnalysisInfo;
import com.ershi.hichat.common.utils.FutureUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.util.Pair;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * url解析器模板
 */
@Slf4j
public abstract class AbstractUrlDiscover implements UrlDiscover {

    /**
     * 识别连接的正则
     */
    private static final Pattern PATTERN = Pattern.compile("((http|https)://)?(www.)?([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");


    @Nullable
    @Override
    public Map<String, UrlAnalysisInfo> getUrlInfoMap(String content) {
        if (StrUtil.isBlank(content)) {
            return new HashMap<>();
        }
        // 提取文本中符合url链接的内容
        List<String> matchUrlList = ReUtil.findAll(PATTERN, content, 0);

        // 并行解析url
        List<CompletableFuture<Pair<String, UrlAnalysisInfo>>> futures = matchUrlList.stream().map(match -> CompletableFuture.supplyAsync(() -> {
            UrlAnalysisInfo urlInfo = getUrlInfo(match);
            return Objects.isNull(urlInfo) ? null : Pair.of(match, urlInfo);
        })).collect(Collectors.toList());
        CompletableFuture<List<Pair<String, UrlAnalysisInfo>>> future = FutureUtils.sequenceNonNull(futures);
        // 结果组装
        return future.join().stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (a, b) -> a));
    }

    @Nullable
    @Override
    public UrlAnalysisInfo getUrlInfo(String url) {
        Document document = getUrlDocument(assembleHttp(url));
        if (Objects.isNull(document)) {
            return null;
        }

        return UrlAnalysisInfo.builder()
                .title(getTitle(document))
                .description(getDescription(document))
                .image(getImage(assembleHttp(url), document)).build();
    }


    /**
     * 拼接http头，方便jsoup解析
     * @param url
     * @return {@link String }
     */
    private String assembleHttp(String url) {

        if (!StrUtil.startWith(url, "http")) {
            return "http://" + url;
        }

        return url;
    }

    /**
     * 解析Url网站内容
     * @param matchUrl
     * @return {@link Document }
     */
    protected Document getUrlDocument(String matchUrl) {
        try {
            Connection connect = Jsoup.connect(matchUrl);
            connect.timeout(2000);
            return connect.get();
        } catch (Exception e) {
            log.error("find error:url:{}", matchUrl, e);
        }
        return null;
    }

    /**
     * 判断链接是否有效
     * 输入链接
     * 返回true或者false
     */
    public static boolean isConnect(String href) {
        // 请求地址
        URL url;
        // 请求状态码
        int state;
        // 下载链接类型
        String fileType;
        try {
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            // 如果成功200，缓存304，移动302都算有效链接，并且不是下载链接
            if ((state == 200 || state == 302 || state == 304) && fileType == null) {
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
