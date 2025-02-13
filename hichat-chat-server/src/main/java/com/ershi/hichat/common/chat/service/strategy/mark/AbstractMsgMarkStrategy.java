package com.ershi.hichat.common.chat.service.strategy.mark;

import com.ershi.hichat.common.chat.dao.MessageMarkDao;
import com.ershi.hichat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.ershi.hichat.common.chat.domain.entity.MessageMark;
import com.ershi.hichat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.ershi.hichat.common.chat.domain.enums.MessageMarkStatusEnum;
import com.ershi.hichat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.ershi.hichat.common.common.event.MessageMarkEvent;
import com.ershi.hichat.common.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;


/**
 * 消息标记策略抽象模板类
 *
 * @author Ershi
 * @date 2025/02/13
 */
public abstract class AbstractMsgMarkStrategy {

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 将标记策略类注册到工厂
     */
    @PostConstruct
    private void init() {
        MsgMarkFactory.register(getTypeEnum().getType(), this);
    }

    /**
     * 获取当前策略类型-子类实现
     *
     * @return {@link MessageMarkTypeEnum }
     */
    protected abstract MessageMarkTypeEnum getTypeEnum();

    /**
     * 标记方法
     *
     * @param uid
     * @param msgId
     */
    @Transactional
    public void mark(Long uid, Long msgId) {
        doMark(uid, msgId);
    }

    /**
     * 取消标记方法
     *
     * @param uid
     * @param msgId
     */
    @Transactional
    public void unMark(Long uid, Long msgId) {
        doUnMark(uid, msgId);
    }

    /**
     * 执行标记-子类实现额外动作
     *
     * @param uid
     * @param msgId
     */
    protected void doMark(Long uid, Long msgId) {
        execute(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    /**
     * 取消标记-子类实现额外动作
     *
     * @param uid
     * @param msgId
     */
    protected void doUnMark(Long uid, Long msgId) {
        execute(uid, msgId, MessageMarkActTypeEnum.UN_MARK);
    }

    /**
     * 标记功能执行方法
     * @param uid
     * @param msgId
     * @param actTypeEnum
     */
    protected void execute(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        // 获取当前标记类型
        Integer markType = getTypeEnum().getType();
        // 获取标记动作类型
        Integer actType = actTypeEnum.getType();
        // 查询是否有旧标记记录
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        // 如果当前动作是取消的类型，数据库一定有记录，没有就直接说明异常，不处理直接跳过
        if (actTypeEnum == MessageMarkActTypeEnum.UN_MARK && Objects.isNull(oldMark)) {
            return;
        }
        // 插入一条新消息,或者修改一条消息 -> 当消息被标记过就是修改，未标记过就是新增，通过oldMark的id判断
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actTypeEnum))
                .build();
        boolean modify = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if (modify) {
            // 修改成功才发布消息标记事件
            ChatMessageMarkDTO dto = new ChatMessageMarkDTO(uid, msgId, markType, actType);
            // 推送给其他用户该消息被标记了
            applicationEventPublisher.publishEvent(new MessageMarkEvent(this, dto));
        }
    }

    /**
     * 将用户标记消息的行为转成对应的消息的状态
     *
     * @param actTypeEnum
     * @return {@link Integer }
     */
    private Integer transformAct(MessageMarkActTypeEnum actTypeEnum) {
        if (actTypeEnum == MessageMarkActTypeEnum.MARK) {
            return MessageMarkStatusEnum.DO.getType();
        } else if (actTypeEnum == MessageMarkActTypeEnum.UN_MARK) {
            return MessageMarkStatusEnum.UN_DO.getType();
        }
        throw new BusinessException("标记动作类型错误，1-确认，2取消");
    }
}
