package com.ershi.hichat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ershi.hichat.common.chat.domain.entity.Contact;
import com.ershi.hichat.common.chat.domain.vo.request.contact.ChatContactPageReq;
import com.ershi.hichat.common.chat.mapper.ContactMapper;
import com.ershi.hichat.common.domain.vo.response.CursorPageBaseResp;
import com.ershi.hichat.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-01-13
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    /**
     * 获取用户在指定房间最后一条读取到的消息
     *
     * @param roomId
     * @param receiveUid
     * @return {@link Contact }
     */
    public Contact get(Long roomId, Long receiveUid) {
        return lambdaQuery()
                .eq(Contact::getRoomId, roomId)
                .eq(Contact::getUid, receiveUid)
                .one();
    }

    /**
     * 查询用户所有会话情况-根据会话最新活跃时间进行会话游标排序
     *
     * @param chatContactPageReq
     * @param uid
     * @return {@link CursorPageBaseResp }<{@link Contact }>
     */
    public CursorPageBaseResp<Contact> getContactPage(ChatContactPageReq chatContactPageReq, Long uid) {
        return CursorUtils.getCursorPageByMysql(this, chatContactPageReq
                , wrapper -> {
                    wrapper.eq(Contact::getUid, uid);
                }, Contact::getActiveTime);
    }

    /**
     * 获取用户在该房间下的收件箱会话信息
     * @param roomIdList
     * @param uid
     * @return {@link List }<{@link Contact }>
     */
    public List<Contact> getByRoomIds(List<Long> roomIdList, Long uid) {
        return lambdaQuery()
                .in(Contact::getRoomId, roomIdList)
                .eq(Contact::getUid, uid)
                .list();
    }

    /**
     * @param roomId
     * @param memberUidList
     * @param msgId
     * @param createTime
     */
    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date createTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, createTime);
    }
}
