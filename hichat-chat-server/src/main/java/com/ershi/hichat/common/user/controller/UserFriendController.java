package com.ershi.hichat.common.user.controller;


import com.ershi.hichat.common.common.domain.vo.ApiResult;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.request.PageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.domain.vo.response.PageBaseResp;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApplyReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApproveReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendCheckReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendDeleteReq;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendApplyResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendCheckResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendUnreadResp;
import com.ershi.hichat.common.user.service.UserFriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 用户联系人表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-24
 */
@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "好友相关接口")
public class UserFriendController {

    @Autowired
    private UserFriendService userFriendService;

    @GetMapping("/page")
    @ApiOperation("获取好友列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq cursorPageBaseReq) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.friendList(uid, cursorPageBaseReq));
    }

    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq friendApplyReq) {
        Long uid = RequestHolder.get().getUid();
        userFriendService.apply(uid, friendApplyReq);
        return ApiResult.success();
    }

    @GetMapping("/check")
    @ApiOperation("批量判断目标是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq friendCheckReq) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.check(uid, friendCheckReq));
    }

    @GetMapping("/apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq pageBaseReq) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.pageApplyFriend(uid, pageBaseReq));
    }

    @PutMapping("/apply")
    @ApiOperation("同意申请")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq friendApproveReq) {
        userFriendService.applyApprove(RequestHolder.get().getUid(), friendApproveReq);
        return ApiResult.success();
    }

    @GetMapping("/apply/unread")
    @ApiOperation("未读申请数")
    public ApiResult<FriendUnreadResp> unread() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.unread(uid));
    }

    @DeleteMapping()
    @ApiOperation("删除好友")
    public ApiResult<Void> delete(@Valid @RequestBody FriendDeleteReq friendDeleteReq) {
        Long uid = RequestHolder.get().getUid();
        userFriendService.deleteFriend(uid, friendDeleteReq.getTargetUid());
        return ApiResult.success();
    }
}

