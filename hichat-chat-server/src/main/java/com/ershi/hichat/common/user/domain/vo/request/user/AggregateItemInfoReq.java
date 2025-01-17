package com.ershi.hichat.common.user.domain.vo.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;


/**
 * 批量查询聚合徽章信息请求体
 * @author Ershi
 * @date 2025/01/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateItemInfoReq {
    @ApiModelProperty(value = "徽章信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @ApiModelProperty(value = "徽章id")
        private Long itemId;
        @ApiModelProperty(value = "最近一次更新徽章信息时间")
        private Long lastModifyTime;
    }
}
