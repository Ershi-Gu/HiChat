package com.ershi.hichat.common.common.aspect;

import com.ershi.hichat.common.common.annotation.AuthCheck;
import com.ershi.hichat.common.common.exception.BusinessErrorEnum;
import com.ershi.hichat.common.common.utils.AssertUtil;
import com.ershi.hichat.common.common.utils.RequestHolder;
import com.ershi.hichat.common.user.domain.enums.RoleEnum;
import com.ershi.hichat.common.user.service.RoleService;
import com.ershi.hichat.common.user.service.UserRoleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 身份校验 AOP
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 */
@Aspect
@Component
public class AuthCheckAspect {

    @Resource
    private UserRoleService userRoleService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object checkAuth(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 要求的权限
        RoleEnum requiredAuth = authCheck.requiredAuth();
        // 校验当前用户权限是否合格
        Long uid = RequestHolder.get().getUid();
        boolean authPass = userRoleService.checkAuth(uid, requiredAuth);
        AssertUtil.isTrue(authPass, BusinessErrorEnum.NO_AUTH);
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

