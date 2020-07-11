package com.roacg.core.model.context;

public interface ContextHolder<C> {

    void clearContext();

    C getContext();

    void setContext(C context);
}
