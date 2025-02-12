package com.ershi.hichat.common.user.controller;


import com.ershi.hichat.common.common.domain.vo.request.IdReqVO;
import com.ershi.hichat.common.common.domain.vo.response.ApiResult;
import com.ershi.hichat.common.common.domain.vo.response.IdRespVO;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.user.domain.vo.request.user.UserEmojiReq;
import com.ershi.hichat.common.user.domain.vo.response.user.UserEmojiResp;
import com.ershi.hichat.common.user.service.UserEmojiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表情包 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-12
 */
@RestController
@RequestMapping("/capi/user/emoji")
@Api(tags = "用户表情包接口")
public class UserEmojiController {

    @Autowired
    private UserEmojiService userEmojiService;

    /**
     * 表情包列表
     *
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @GetMapping("/list")
    @ApiOperation("表情包列表")
    public ApiResult<List<UserEmojiResp>> getEmojisPage() {
        return ApiResult.success(userEmojiService.list(RequestHolder.get().getUid()));
    }


    /**
     * 新增表情包
     *
     * @param userEmojiReq
     * @return 表情包
     */
    @PostMapping()
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> insertEmojis(@Valid @RequestBody UserEmojiReq userEmojiReq) {
        return ApiResult.success(userEmojiService.insert(userEmojiReq, RequestHolder.get().getUid()));
    }

    /**
     * 删除表情包
     *
     * @return 删除结果
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @DeleteMapping()
    @ApiOperation("删除表情包")
    public ApiResult<Void> deleteEmojis(@Valid @RequestBody IdReqVO reqVO) {
        userEmojiService.remove(reqVO.getId(), RequestHolder.get().getUid());
        return ApiResult.success();
    }
}

