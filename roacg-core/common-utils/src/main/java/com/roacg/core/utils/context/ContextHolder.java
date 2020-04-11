package com.roacg.core.utils.context;

public interface ContextHolder<C> {

    void clearContext();

    C getContext();

    void setContext(C context);
}
