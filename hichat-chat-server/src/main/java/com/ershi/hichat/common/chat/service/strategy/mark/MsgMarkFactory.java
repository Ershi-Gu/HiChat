package com.ershi.hichat.common.chat.service.strategy.mark;


import com.ershi.hichat.common.common.exception.SystemCommonErrorEnum;
import com.ershi.hichat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 消息标记策略工厂
 * @author Ershi
 * @date 2025/02/13
 */
public class MsgMarkFactory {

    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    /**
     * 注册策略类到工厂map中
     * @param markType
     * @param strategy
     */
    public static void register(Integer markType, AbstractMsgMarkStrategy strategy) {
        STRATEGY_MAP.put(markType, strategy);
    }

    /**
     * 获取策略类
     * @param markType
     * @return {@link AbstractMsgMarkStrategy }
     */
    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType) {
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, SystemCommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
