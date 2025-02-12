package com.ershi.hichat.common.user.service;

import com.ershi.hichat.common.common.domain.vo.response.ApiResult;
import com.ershi.hichat.common.common.domain.vo.response.IdRespVO;
import com.ershi.hichat.common.user.domain.entity.UserEmoji;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ershi.hichat.common.user.domain.vo.request.user.UserEmojiReq;
import com.ershi.hichat.common.user.domain.vo.response.user.UserEmojiResp;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-12
 */
public interface UserEmojiService {

    List<UserEmojiResp> list(Long uid);

    IdRespVO insert(UserEmojiReq userEmojiReq, Long uid);

    void remove(long id, Long uid);
}
