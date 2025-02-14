package com.ershi.hichat.common.chat.service.strategy.msg.handler;

import cn.hutool.core.bean.BeanUtil;
import com.ershi.hichat.common.chat.dao.MessageDao;
import com.ershi.hichat.common.chat.domain.entity.Message;
import com.ershi.hichat.common.chat.domain.entity.msg.BaseMsgDTO;
import com.ershi.hichat.common.chat.domain.enums.MessageTypeEnum;
import com.ershi.hichat.common.chat.domain.vo.request.msg.ChatMessageReq;
import com.ershi.hichat.common.chat.service.adapter.MessageAdapter;
import com.ershi.hichat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.ershi.hichat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * 消息处理器抽象父类 - 用于规范和接管所有类型的消息处理器
 * @author Ershi
 * @date 2025/01/14
 */
@Component
public abstract class AbstractMsgHandler <Req>{

    @Autowired
    private MessageDao messageDao;

    /**
     * 消息体类型保存
     */
    private Class<Req> messageBodyClass;

    /**
     * 消息处理器初始化方法，继承了该类的所有子类都会执行该方法，用于初始化消息处理器，并注册到消息处理器工厂中
     */
    @PostConstruct
    private void init() {
        // 获取泛型类型保存
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.messageBodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        // 将消息处理器注册到工厂中，k-v:消息类型-消息处理器
        MsgHandlerFactory.register(getMessageTypeEnum().getType(), this);
    }

    /**
     * 获取消息类型
     * @return {@link MessageTypeEnum }
     */
    protected abstract MessageTypeEnum getMessageTypeEnum();

    /**
     * 消息检查规则
     * @param messageBody
     * @param roomId
     * @param uid
     */
    protected abstract void checkMsg(Req messageBody, Long roomId, Long uid);

    /**
     * 消息额外信息保存-子类实现
     * @param message
     * @param messageBody
     */
    protected abstract void saveMsg(Message message, Req messageBody);

    /**
     * 展示消息
     *
     * @param msg
     * @return {@link Object }
     */
    public abstract BaseMsgDTO showMsg(Message msg);

    /**
     * 当消息被回复时展示形态
     * @param replyMessage
     */
    public abstract Object showReplyMsg(Message replyMessage);

    /**
     * 消息展示在会话列表上的形式
     * @param message
     * @return {@link String }
     */
    public abstract String showMsgOnContact(Message message);



    /**
     * 检查消息合法性，并持久化消息到服务器
     * @param chatMessageReq 发送来的消息体
     * @param uid 发送者uid
     * @return {@link Long } 消息持久化后的msgId
     */
    @Transactional(rollbackFor = Exception.class)
    public Long checkAndSaveMsg(ChatMessageReq chatMessageReq, Long uid) {
        // 获取消息体的正确类型
        Req messageBody = this.toBean(chatMessageReq.getMessageBody());

        // 消息基本注解校验，校验ChatMessageReq中messageBody的validate注解校验
        AssertUtil.allCheckValidateThrow(messageBody);
        // 子类扩展校验规则
        checkMsg(messageBody, chatMessageReq.getRoomId(), uid);

        // 消息基本信息保存
        Message insert = MessageAdapter.buildMsgSave(chatMessageReq, uid);
        messageDao.save(insert);

        // 子类根据不同的消息类型保存extra扩展信息
        saveMsg(insert, messageBody);
        return insert.getId();
    }

    /**
     * 将消息转换成具体的消息类型
     * @param body
     * @return {@link Req }
     */
    private Req toBean (Object body) {
        return BeanUtil.toBean(body, messageBodyClass);
    }
}
