package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.common.domain.vo.response.IdRespVO;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.user.dao.UserEmojiDao;
import com.ershi.hichat.common.user.domain.entity.UserEmoji;
import com.ershi.hichat.common.user.domain.vo.request.user.UserEmojiReq;
import com.ershi.hichat.common.user.domain.vo.response.user.UserEmojiResp;
import com.ershi.hichat.common.user.service.UserEmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEmojiServiceImpl implements UserEmojiService {

    @Autowired
    private UserEmojiDao userEmojiDao;

    /**
     * 获取表情包列表
     *
     * @param uid
     * @return {@link List }<{@link UserEmojiResp }>
     */
    @Override
    public List<UserEmojiResp> list(Long uid) {
        return userEmojiDao.listUserEmoji(uid)
                .stream()
                .map(userEmoji -> UserEmojiResp.builder()
                        .id(userEmoji.getId())
                        .expressionUrl(userEmoji.getExpressionUrl())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 添加表情包
     * @param userEmojiReq
     * @param uid
     * @return {@link IdRespVO }
     */
    @Override
    public IdRespVO insert(UserEmojiReq userEmojiReq, Long uid) {
        //校验表情数量是否超过30
        int count = userEmojiDao.countByUid(uid);
        AssertUtil.isFalse(count > 30, "最多只能添加30个表情哦~~");
        //校验表情是否存在
        Integer existsCount = userEmojiDao.lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, userEmojiReq.getExpressionUrl())
                .eq(UserEmoji::getUid, uid)
                .count();
        AssertUtil.isFalse(existsCount > 0, "当前表情已存在哦~~");
        // 入库表情包地址
        UserEmoji insert = UserEmoji.builder().uid(uid).expressionUrl(userEmojiReq.getExpressionUrl()).build();
        userEmojiDao.save(insert);
        return IdRespVO.id(insert.getId());
    }

    @Override
    public void remove(long id, Long uid) {
        UserEmoji userEmoji = userEmojiDao.getById(id);
        AssertUtil.isNotEmpty(userEmoji, "表情不能为空");
        AssertUtil.equal(userEmoji.getUid(), uid, "小黑子，别人表情不是你能删的");
        userEmojiDao.removeById(id);
    }
}
