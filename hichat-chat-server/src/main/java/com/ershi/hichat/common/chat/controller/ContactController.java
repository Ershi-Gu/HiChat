package com.ershi.hichat.common.chat.controller;


import com.ershi.hichat.common.chat.domain.vo.request.contact.ChatContactPageReq;
import com.ershi.hichat.common.chat.domain.vo.response.contact.ChatContactResp;
import com.ershi.hichat.common.chat.service.RoomContactService;
import com.ershi.hichat.common.common.domain.vo.response.ApiResult;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 会话列表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天会话相关接口")
public class ContactController {

    @Autowired
    private RoomContactService roomContactService;

    @GetMapping("/public/contact/page")
    @ApiOperation("会话列表")
    public ApiResult<CursorPageBaseResp<ChatContactResp>> getRoomPage(@Valid ChatContactPageReq chatContactPageReq) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomContactService.getContactPage(chatContactPageReq, uid));
    }
}

