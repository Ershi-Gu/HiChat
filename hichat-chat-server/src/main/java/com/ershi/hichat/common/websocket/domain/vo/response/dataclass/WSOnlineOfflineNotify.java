package com.ershi.hichat.common.websocket.domain.vo.response.dataclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户上下线变动的推送
 * @author Ershi
 * @date 2024/11/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {
    /**
     * 新的上下线用户
     */
    private List<ChatMemberResp> changeList = new ArrayList<>();
    /**
     * 在线人数
     */
    private Long onlineNum;
}
