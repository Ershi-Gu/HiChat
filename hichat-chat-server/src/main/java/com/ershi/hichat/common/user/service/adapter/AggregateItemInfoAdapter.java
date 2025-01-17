package com.ershi.hichat.common.user.service.adapter;

import com.ershi.hichat.common.user.domain.entity.ItemConfig;
import com.ershi.hichat.common.user.domain.vo.response.user.AggregateItemInfoResp;

/**
 * 聚合徽章信息构建器
 * @author Ershi
 * @date 2025/01/17
 */
public class AggregateItemInfoAdapter {

    /**
     * 构建聚合徽章信息返回
     * @param itemConfig
     * @return {@link AggregateItemInfoResp }
     */
    public static AggregateItemInfoResp buildResp(ItemConfig itemConfig) {
        return AggregateItemInfoResp.builder()
                .itemId(itemConfig.getId())
                .img(itemConfig.getImg())
                .describe(itemConfig.getDescribe())
                .build();
    }
}
