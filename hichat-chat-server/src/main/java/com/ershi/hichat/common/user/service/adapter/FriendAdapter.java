package com.ershi.hichat.common.user.service.adapter;

import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.vo.response.FriendResp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友相关模块适配器
 *
 * @author Ershi
 * @date 2024/12/25
 */
public class FriendAdapter {

    /**
     * 将User转换成FriendResp返回
     * @param friendUserList
     * @return {@link List}<{@link FriendResp}>
     */
    public static List<FriendResp> buildFriendRespList(List<User> friendUserList) {
        // 使用流处理将每个User对象转换为FriendResp对象，并收集到一个新的List中
        return friendUserList.stream().map(friendUser -> {
            FriendResp resp = new FriendResp();
            resp.setUid(friendUser.getId());
            resp.setActiveStatus(friendUser.getActiveStatus());
            return resp;
        }).collect(Collectors.toList());
    }
}
