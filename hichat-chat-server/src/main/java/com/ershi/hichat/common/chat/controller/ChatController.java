package com.ershi.hichat.common.chat.controller;


import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessagePageReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageRecallReq;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageReq;
import com.ershi.hichat.common.chat.domain.vo.response.ChatMessageResp;
import com.ershi.hichat.common.chat.service.ChatService;
import com.ershi.hichat.common.common.domain.vo.response.ApiResult;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/public/msg/page")
    @ApiOperation("消息列表")
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq chatMessagePageReq) {
        // 获取消息列表数据
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(chatMessagePageReq, RequestHolder.get().getUid());
        // 过滤掉黑名单用户发送的消息 -> 主要是防止黑名单更新不及时
        chatService.filterBlackMsg(msgPage);
        return ApiResult.success(msgPage);
    }

    @PutMapping("/msg/recall")
    @ApiOperation("撤回消息")
    public ApiResult<Void> recallMsg(@Valid @RequestBody ChatMessageRecallReq chatMessageRecallReq) {
        chatService.recallMsg(RequestHolder.get().getUid(), chatMessageRecallReq);
        return ApiResult.success();
    }
}
