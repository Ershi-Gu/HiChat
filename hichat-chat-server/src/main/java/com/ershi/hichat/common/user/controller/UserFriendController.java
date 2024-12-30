package com.ershi.hichat.common.user.controller;


import com.ershi.hichat.common.common.domain.vo.ApiResult;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendApplyReq;
import com.ershi.hichat.common.user.domain.vo.request.friend.FriendCheckReq;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendCheckResp;
import com.ershi.hichat.common.user.domain.vo.response.friend.FriendResp;
import com.ershi.hichat.common.user.service.UserFriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/check")
    @ApiOperation("批量判断目标是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq friendCheckReq) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.check(uid, friendCheckReq));
    }


    @GetMapping("/page")
    @ApiOperation("获取联系人列表")
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
}

