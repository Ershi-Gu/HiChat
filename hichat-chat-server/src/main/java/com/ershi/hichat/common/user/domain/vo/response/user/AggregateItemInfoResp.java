package com.ershi.hichat.common.user.domain.vo.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聚合徽章信息
 * @author Ershi
 * @date 2025/01/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateItemInfoResp {

    @ApiModelProperty(value = "徽章id")
    private Long itemId;
    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;
    @ApiModelProperty("徽章图像")
    private String img;
    @ApiModelProperty("徽章说明")
    private String describe;

    /**
     * 当徽章信息不需要刷新时调用该方法
     * @param itemId
     * @return {@link AggregateItemInfoResp }
     */
    public static AggregateItemInfoResp skip(Long itemId) {
        AggregateItemInfoResp resp = new AggregateItemInfoResp();
        resp.setItemId(itemId);
        resp.setNeedRefresh(Boolean.FALSE);
        return resp;
    }
}

