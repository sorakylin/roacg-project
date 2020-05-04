package com.roacg.core.model.consts;

public final class RoAuthConst {

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    //payload 中用户信息的key
    public static final String TOKEN_USER_KEY = "rouser";

    /**
     * 服务暴露的资源路径前缀
     */
    public static final String EXPOSE_RESOURCE_KEY_PREFIX = "RO@EXPOSE-RESOURCE:";


    public static String getExposeResourceKey(String serviceName) {
        return EXPOSE_RESOURCE_KEY_PREFIX + serviceName;
    }
}
