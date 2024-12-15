package com.ershi.hichat.common.common.annotation;

import com.ershi.hichat.common.user.domain.enums.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于权限校验当前用户
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi-Gu</a>
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 要求权限，默认要求超管权限
     *
     * @return {@link RoleEnum}
     */
    RoleEnum requiredAuth() default RoleEnum.ADMIN;

}