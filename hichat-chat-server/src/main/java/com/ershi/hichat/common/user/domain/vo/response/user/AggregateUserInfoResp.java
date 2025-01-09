package com.ershi.hichat.common.user.domain.vo.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 聚合用户信息返回-用于前端进行懒加载
 *
 * @author Ershi-Gu
 * @date 2025/01/09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregateUserInfoResp {
    @ApiModelProperty(value = "用户id")
    private Long uid;
    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;
    @ApiModelProperty(value = "用户昵称")
    private String name;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "归属地")
    private String locPlace;
    @ApiModelProperty("佩戴的徽章id")
    private Long wearingItemId;
    @ApiModelProperty(value = "用户拥有的徽章id列表")
    List<Long> itemIds;

    /**
     * 当前不需要刷新时调用该方法
     * @param uid
     * @return {@link AggregateUserInfoResp }
     */
    public static AggregateUserInfoResp skip(Long uid) {
        AggregateUserInfoResp resp = new AggregateUserInfoResp();
        resp.setUid(uid);
        resp.setNeedRefresh(Boolean.FALSE);
        return resp;
    }
}
