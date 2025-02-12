package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.user.domain.entity.UserEmoji;
import com.ershi.hichat.common.user.domain.vo.response.user.UserEmojiResp;
import com.ershi.hichat.common.user.mapper.UserEmojiMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-12
 */
@Service
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji>{

    /**
     * 查询用户表情包
     * @param uid
     * @return {@link List }<{@link UserEmojiResp }>
     */
    public List<UserEmoji> listUserEmoji(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).list();
    }

    public int countByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).count();
    }
}
