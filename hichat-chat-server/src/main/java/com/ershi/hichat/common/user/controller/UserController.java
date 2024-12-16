package com.ershi.hichat.common.user.controller;


import com.ershi.hichat.common.common.annotation.AuthCheck;
import com.ershi.hichat.common.common.domain.vo.ApiResult;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.user.domain.enums.RoleEnum;
import com.ershi.hichat.common.user.domain.vo.request.BlackReq;
import com.ershi.hichat.common.user.domain.vo.request.ModifyNameReq;
import com.ershi.hichat.common.user.domain.vo.request.WearingBadgesReq;
import com.ershi.hichat.common.user.domain.vo.response.user.BadgeResp;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;
import com.ershi.hichat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-11-25
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户个人信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq modifyNameReq) {
       userService.modifyName(RequestHolder.get().getUid(), modifyNameReq.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badges")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadges(@RequestBody WearingBadgesReq wearingBadgesReq) {
        userService.wearingBadges(wearingBadgesReq.getBadgeId());
        return ApiResult.success();
    }

    @PutMapping("/blackUser")
    @AuthCheck(requiredAuth = RoleEnum.ADMIN)
    @ApiOperation("拉黑用户")
    public ApiResult<Void> blackUser(@Valid @RequestBody BlackReq blackReq) {
        userService.blackUser(blackReq.getUid());
        return ApiResult.success();
    }

    @PutMapping("/blackUserAndIp")
    @AuthCheck(requiredAuth = RoleEnum.ADMIN)
    @ApiOperation("拉黑用户及其ip")
    public ApiResult<Void> blackUserAndIp(@Valid @RequestBody BlackReq blackReq) {
        userService.blackUserAndIp(blackReq.getUid());
        return ApiResult.success();
    }

}

