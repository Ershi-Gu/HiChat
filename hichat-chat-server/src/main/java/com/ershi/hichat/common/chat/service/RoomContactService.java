package com.ershi.hichat.common.chat.service;


import com.ershi.hichat.common.chat.domain.vo.request.contact.ChatContactPageReq;
import com.ershi.hichat.common.chat.domain.vo.response.contact.ChatContactResp;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;

import javax.validation.Valid;

/**
 * 用户会话房间服务支持类
 * @author Ershi
 * @date 2025/02/13
 */
public interface RoomContactService {

    CursorPageBaseResp<ChatContactResp> getContactPage(@Valid ChatContactPageReq chatContactPageReq, Long uid);
}
