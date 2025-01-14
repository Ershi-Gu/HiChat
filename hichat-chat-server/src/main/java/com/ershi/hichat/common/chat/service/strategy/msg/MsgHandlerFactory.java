package com.ershi.hichat.common.chat.service.strategy.msg;

import com.ershi.hichat.common.chat.service.strategy.msg.handler.AbstractMsgHandler;
import com.ershi.hichat.common.common.exception.SystemCommonErrorEnum;
import com.ershi.hichat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息处理器工厂 - 用来加载不同的消息处理器
 * @author Ershi
 * @date 2025/01/14
 */
public class MsgHandlerFactory {

    /**
     * 保存各个类型消息对应的处理器 <br>
     * k:v-消息类型整型-消息处理器
     */
    private static final Map<Integer, AbstractMsgHandler> msgHandlerMap = new HashMap<>();

    /**
     * 将消息处理器注册到工厂中
     * @param msgType
     * @param msgHandler
     */
    public static <Req> void register(Integer msgType, AbstractMsgHandler<Req> msgHandler) {
        msgHandlerMap.put(msgType, msgHandler);
    }

    /**
     * 根据消息类型获取消息处理器
     * @param msgType
     * @return {@link AbstractMsgHandler }
     */
    public static AbstractMsgHandler getMsgHandlerNoNull(Integer msgType) {
        AbstractMsgHandler msgHandler = msgHandlerMap.get(msgType);
        AssertUtil.isNotEmpty(msgHandler, SystemCommonErrorEnum.PARAM_VALID);
        return msgHandler;
    }
}
