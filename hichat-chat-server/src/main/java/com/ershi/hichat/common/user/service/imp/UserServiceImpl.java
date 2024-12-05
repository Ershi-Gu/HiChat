package com.ershi.hichat.common.user.service.imp;

import com.ershi.hichat.common.common.exception.BusinessException;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.user.dao.UserBackpackDao;
import com.ershi.hichat.common.user.dao.UserDao;
import com.ershi.hichat.common.user.domain.entity.User;
import com.ershi.hichat.common.user.domain.entity.UserBackpack;
import com.ershi.hichat.common.user.domain.enums.ItemEnum;
import com.ershi.hichat.common.user.domain.vo.request.ModifyNameRequest;
import com.ershi.hichat.common.user.domain.vo.response.user.UserInfoResp;
import com.ershi.hichat.common.user.service.UserService;
import com.ershi.hichat.common.user.service.adapter.UserAdapter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBackpackDao userBackpackDao;

    /**
     * 注册
     *
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
     *
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

    /**
     * 修改用户名
     *
     * @param uid
     * @param name
     */
    @Override
    @Transactional
    public void modifyName(Long uid, String name) {
        // 判断名字是否重复
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "名字已存在，换个名字吧~");
        // 判断改名卡是否够用
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不够咯，暂时无法改名啦~");
        // 使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) { // 通过数据库的行级锁实现乐观锁
            // 改名
            userDao.modifyName(uid, name);
        }
    }

}
