package com.ershi.hichat.common.common.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 通用id返回
 * @author Ershi
 * @date 2025/02/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdRespVO {
    @ApiModelProperty("id")
    private long id;

    public static IdRespVO id(Long id) {
        IdRespVO idRespVO = new IdRespVO();
        idRespVO.setId(id);
        return idRespVO;
    }
}
