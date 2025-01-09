package com.ershi.hichat.common.user.domain.vo.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;


/**
 * 批量查询用户汇总详情请求体
 * @author Ershi
 * @date 2025/01/09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregateUserInfoReq {
    @ApiModelProperty(value = "用户信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    /**
     * 用户信息
     * @author Ershi
     * @date 2025/01/09
     */
    @Data
    public static class infoReq {
        @ApiModelProperty(value = "uid")
        private Long uid;
        @ApiModelProperty(value = "最近一次更新用户信息时间")
        private Long lastModifyTime;
    }
}
