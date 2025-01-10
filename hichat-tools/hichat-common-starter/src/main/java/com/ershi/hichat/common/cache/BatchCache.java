package com.ershi.hichat.common.cache;

import java.util.List;
import java.util.Map;

/**
 * 批量缓存规范接口
 *
 * @author Ershi
 * @date 2025/01/10
 */
public interface BatchCache<IN, OUT> {
    /**
     * 获取单个
     *
     * @param req
     * @return {@link OUT }
     */
    OUT get(IN req);

    /**
     * 获取批量
     */
    Map<IN, OUT> getBatch(List<IN> req);

    /**
     * 删除单个
     */
    void delete(IN req);

    /**
     * 删除多个
     */
    void deleteBatch(List<IN> req);
}
