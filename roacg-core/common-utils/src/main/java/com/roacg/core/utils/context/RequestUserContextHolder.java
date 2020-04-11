package com.roacg.core.utils.context;

import com.roacg.core.model.auth.RequestUser;

class RequestUserContextHolder implements ContextHolder<RequestUser> {

    private static final ThreadLocal<RequestUser> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public RequestUser getContext() {
        RequestUser ctx = contextHolder.get();

        if (ctx == null) {
            ctx = RequestUser.guest();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    @Override
    public void setContext(RequestUser context) {
        contextHolder.set(context);
    }

}
