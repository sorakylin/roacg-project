package com.roacg.core.utils.context;

import com.roacg.core.model.auth.RequestUser;

/**
 * 整个应用的上下文
 * ThreadLocal实现
 * <p>
 * Create by skypyb on 2020-04-11
 */
public class RoContext {

    private static final ContextHolder<RequestUser> userContextHolder = new RequestUserContextHolder();


    public static RequestUser getRequestUser() {
        return userContextHolder.getContext();
    }

    public static void setRequestUser(RequestUser user) {
        userContextHolder.setContext(user);
    }

    public static void clearRequestUser() {
        userContextHolder.clearContext();
    }


}
