package com.ershi.hichat.common.user.service.adapter;

import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserApply;
import com.ershi.hichat.common.user.domain.entity.UserFriend;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApplyReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendCheckReq;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendApplyResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendCheckResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendResp;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ershi.hichat.common.user.domain.enums.ApplyReadStatusEnum.*;
import static com.ershi.hichat.common.user.domain.enums.ApplyStatusEnum.*;
import static com.ershi.hichat.common.user.domain.enums.ApplyTypeEnum.*;

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


    /**
     * 构建好友验证返回
     * @param friendList
     * @return {@link FriendCheckResp}
     */
    public static FriendCheckResp buildFriendCheckResp(List<UserFriend> friendList, FriendCheckReq friendCheckReq) {
        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = friendCheckReq.getUidList().stream()
                .map(friendUid -> {
                    FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
                    friendCheck.setUid(friendUid);
                    friendCheck.setIsFriend(friendUidSet.contains(friendUid));
                    return friendCheck;
                }).collect(Collectors.toList());
        return FriendCheckResp.builder().checkedList(friendCheckList).build();
    }

    /**
     * 构建好友信息
     *
     * @param uid
     * @param friendApplyReq
     * @return {@link UserApply}
     */
    public static UserApply buildFriendApply(Long uid, FriendApplyReq friendApplyReq) {
        UserApply userApplyNew = new UserApply();
        userApplyNew.setUid(uid);
        userApplyNew.setMsg(friendApplyReq.getMsg());
        userApplyNew.setType(ADD_FRIEND.getType());
        userApplyNew.setTargetId(friendApplyReq.getTargetUid());
        userApplyNew.setStatus(WAIT_APPROVAL.getStatus());
        userApplyNew.setReadStatus(UNREAD.getStatus());
        return userApplyNew;
    }

    /**
     * 构建好友信息列表返回数据
     * @param records
     * @return {@link List}<{@link FriendApplyResp}>
     */
    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream().map(userApply -> {
            FriendApplyResp friendApplyResp = new FriendApplyResp();
            friendApplyResp.setUid(userApply.getUid());
            friendApplyResp.setType(userApply.getType());
            friendApplyResp.setApplyId(userApply.getId());
            friendApplyResp.setMsg(userApply.getMsg());
            friendApplyResp.setStatus(userApply.getStatus());
            return friendApplyResp;
        }).collect(Collectors.toList());
    }
}
