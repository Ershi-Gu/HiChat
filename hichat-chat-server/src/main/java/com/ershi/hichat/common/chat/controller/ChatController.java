package com.ershi.hichat.common.chat.controller;


import com.ershi.hichat.common.chat.domain.vo.request.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.chat.service.ChatService;
import com.ershi.hichat.common.common.domain.vo.ApiResult;
import com.ershi.hichat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 聊天相关接口
 * @author Ershi
 * @date 2025/01/13
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/msg")
    @ApiOperation("发送消息")
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq chatMessageReq) {
        Long msgId = chatService.sendMsg(chatMessageReq, RequestHolder.get().getUid());
        // 封装完整的消息展示格式，方便前端直接获取，不用等待websocket重新推送
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }
}
