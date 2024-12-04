package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.user.dao.UserBackpackDao;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.enums.ItemEnum;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;
import com.ershi.hichat.common.user.service.UserService;
import com.ershi.hichat.common.user.service.adapter.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBackpackDao userBackpackDao;

    /**
     * 注册
     * @param insert
     * @return {@link Long}
     */
    @Override
    @Transactional
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        // todo 用户注册的事件
        return null;

    }

    /**
     * 获取当前登录用户信息
     * @param uid
     * @return {@link UserInfoResp}
     */
    @Override
    public UserInfoResp getUserInfo(Long uid) {
        // 获取用户基本信息
        User userInfo = userDao.getById(uid);
        // 获取剩余改名次数
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        // 构建用户信息返回
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

}
