package com.roacg.service.gateway.filter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 代理默认的授权过滤器 {@link AuthorizationWebFilter}
 * 实现拒绝请求后返回自定义的信息和状态码功能
 * <p>
 * Create by skypyb on 2020-04-04
 */
public class RoAuthorizationWebFilterProxy extends AuthorizationWebFilter implements WebFilter {


    private ServerAccessDeniedHandler accessDeniedHandler;

    public RoAuthorizationWebFilterProxy(ReactiveAuthorizationManager<? super ServerWebExchange> accessDecisionManager,
                                         ServerAccessDeniedHandler accessDeniedHandler) {
        super(accessDecisionManager);
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return super.filter(exchange, chain)
                .onErrorResume(AccessDeniedException.class, e -> this.accessDeniedHandler.handle(exchange, e));
    }
}