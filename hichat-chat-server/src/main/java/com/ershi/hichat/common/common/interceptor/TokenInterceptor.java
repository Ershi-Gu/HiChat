package com.ershi.hichat.common.common.interceptor;

import com.ershi.hichat.common.common.exception.HttpErrorEnum;
import com.ershi.hichat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * 鉴权拦截器（对所有路径执行拦截，包括公共接口）
 *
 * @author Ershi
 * @date 2024/12/04
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    /**
     * 携带token的请求头名
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";
    /**
     * token前缀
     */
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    /**
     * request域中uid的key
     */
    public static final String ATTRIBUTE_UID = "uid";
    /**
     * 公共接口路径最短长度
     */
    public static final int PUBLIC_PATH_MIN_LENGTH = 3;
    /**
     *公共接口/public所在位置
     */
    public static final int PUBLIC_PATH_INDEX = 3;

    @Autowired
    private LoginService loginService;

    /**
     * 请求前校验登录态
     * @param request
     * @param response
     * @param handler
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 Token
        String token = getToken(request);
        // 判断登录态是否存在
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            // 有登录态,设置登录态到 request 中
            request.setAttribute(ATTRIBUTE_UID, validUid);
        } else {
            // 判断是否是公共接口
            boolean isPublicURI = isPublicURI(request);
            // 无登录态又不是公共接口，返回401
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;

    }

    /**
     * 判断请求的是否是公共接口
     * @param request
     * @return boolean
     */
    private boolean isPublicURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] splitPath = requestURI.split("/");
        return splitPath.length > PUBLIC_PATH_MIN_LENGTH && "public".equals(splitPath[PUBLIC_PATH_INDEX]);
    }

    /**
     * 从请求中获取 Token
     *
     * @param request
     * @return {@link String} token
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);
    }
}
