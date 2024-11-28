package com.ershi.hichat.common.user.domain.vo.response.ws.dataclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功返回信息
 *
 * @author Ershi
 * @date 2024/11/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccess {
    /**
     * uid
     */
    private Long uid;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 登录凭证
     */
    private String token;
    /**
     * 名字
     */
    private String name;
    /**
     * todo 用户权限 0普通用户 1超管
     */
    private Integer power;
}
