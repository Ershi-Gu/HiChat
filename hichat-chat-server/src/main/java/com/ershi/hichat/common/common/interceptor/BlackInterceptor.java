package com.ershi.hichat.common.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.ershi.hichat.common.common.domain.dto.RequestInfo;
import com.ershi.hichat.common.common.exception.HttpErrorEnum;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.user.domain.enums.BlackTypeEnum;
import com.ershi.hichat.common.user.service.cache.UserInfoCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 黑名单拦截器，拦截被封禁用户的所有请求
 *
 * @author Ershi
 * @date 2024/12/04
 */
@Slf4j
@Component
public class BlackInterceptor implements HandlerInterceptor {

    @Autowired
    private UserInfoCache userInfoCache;

    /**
     * 检测当前请求用户或ip是否在黑名单表中，若在则不允许请求
     * @param request
     * @param response
     * @param handler
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取黑名单表
        Map<Integer, Set<String>> blackMap = userInfoCache.getBlackMap();

        // 判断当前请求用户uid或ip是否在黑名单表中
        RequestInfo requestInfo = RequestHolder.get();
        if (inBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))) {
            HttpErrorEnum.USER_FORBIDDEN.sendHttpError(response);
            return false;
        }
        if (inBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()))) {
            HttpErrorEnum.USER_FORBIDDEN.sendHttpError(response);
            return false;
        }

        return true;
    }

    /**
     * 判断目标是否在黑名单表中
     * @param target
     * @param blackSet
     * @return boolean
     */
    private boolean inBlackList(Object target, Set<String> blackSet) {
        if (Objects.isNull(target) || CollectionUtil.isEmpty(blackSet)) {
            return false;
        }
        return blackSet.contains(target.toString());
    }
}
