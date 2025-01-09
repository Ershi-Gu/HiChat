package com.ershi.hichat.common.common.constant;

/**
 * 管理Redis中存放的key
 *
 * @author Ershi
 * @date 2024/11/29
 */
public class RedisKey {

    /**
     * 项目基础key
     */
    private static final String BASE_KEY = "hichat:chat:";

    /**
     * 用户tokenKey
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    /**
     * 用户的信息最後一次更新时间
     */
    public static final String USER_LAST_MODIFY_STRING = "userLastModifyTime:uid_%d";

    /**
     * 获取key
     *
     * @param key 带有模板字符串的key
     * @param o   填入模板的参数
     * @return {@link String} 业务前缀key + 参数
     */
    public static String getKey(String key, Object... o) {
        return BASE_KEY + String.format(key, o);
    }
}
