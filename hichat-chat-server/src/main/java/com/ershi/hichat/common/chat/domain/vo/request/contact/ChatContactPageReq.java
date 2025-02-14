package com.ershi.hichat.common.chat.domain.vo.request.contact;

import com.ershi.hichat.common.domain.vo.request.CursorPageBaseReq;
import lombok.*;

/**
 * 获取会话列表请求
 *
 * @author Ershi
 * @date 2025/02/07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ChatContactPageReq extends CursorPageBaseReq {

}
