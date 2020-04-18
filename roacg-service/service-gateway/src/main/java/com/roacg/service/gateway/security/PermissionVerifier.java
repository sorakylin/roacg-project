package com.roacg.service.gateway.security;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import java.util.HashSet;
import java.util.Set;


public class PermissionVerifier {


    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final Set<String> PERMIT_ALL = new HashSet<>();


    /**
     * 校验是否属于可放行的资源
     *
     * @param requestPath 请求路径
     * @return
     */
    public static boolean permitResource(String requestPath, HttpMethod method) {
        return PERMIT_ALL.stream().filter(r -> ANT_PATH_MATCHER.match(r, requestPath)).findFirst().isPresent();
    }


    public static boolean checkResource(String requestPath, Set<String> roles) {

        return PERMIT_ALL.stream().filter(r -> ANT_PATH_MATCHER.match(r, requestPath)).findFirst().isPresent();
    }


}
