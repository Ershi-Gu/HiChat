package com.ershi.hichat.common.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.user.domain.entity.UserApply;
import com.ershi.hichat.common.user.domain.enums.ApplyReadStatusEnum;
import com.ershi.hichat.common.user.domain.enums.ApplyStatusEnum;
import com.ershi.hichat.common.user.domain.enums.ApplyTypeEnum;
import com.ershi.hichat.common.user.mapper.UserApplyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ershi.hichat.common.user.domain.enums.ApplyStatusEnum.*;
import static com.ershi.hichat.common.user.domain.enums.ApplyReadStatusEnum.*;

/**
 * <p>
 * 申请表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2024-12-24
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply>{

    /**
     * 获取好友申请记录
     * @param uid
     * @param targetUid
     * @return {@link UserApply}
     */
    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery().eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetUid)
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVAL)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getType())
                .one();
    }

    /**
     * 获取未读申请数
     * @param targetId 目标用户id
     * @return {@link Integer}
     */
    public Integer getUnReadCount(Long targetId) {
        return lambdaQuery().eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getStatus())
                .count();
    }

    /**
     * 批准好友请求通过
     * @param applyId
     */
    public void agree(Long applyId) {
        lambdaUpdate().set(UserApply::getStatus, AGREE.getStatus())
                .eq(UserApply::getId, applyId)
                .update();
    }

    /**
     * 分页查询好友申请数据
     * @param uid
     * @param plusPage
     * @return {@link IPage}<{@link UserApply}>
     */
    public IPage<UserApply> friendApplyPage(Long uid, Page plusPage) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getType())
                .orderByDesc(UserApply::getCreateTime)
                .page(plusPage);
    }

    /**
     * 已读申请消息
     * @param uid
     * @param applyIds
     */
    public void readApples(Long uid, List<Long> applyIds) {
        lambdaUpdate()
                .set(UserApply::getReadStatus, READ.getStatus())
                .eq(UserApply::getReadStatus, UNREAD.getStatus())
                .in(UserApply::getId, applyIds)
                .eq(UserApply::getTargetId, uid)
                .update();
    }
}
