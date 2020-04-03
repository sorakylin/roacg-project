package com.roacg.service.gateway.filter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class RoAuthorizationWebFilter implements WebFilter {

    private ReactiveAuthorizationManager<? super ServerWebExchange> accessDecisionManager;

    private ServerAccessDeniedHandler accessDeniedHandler;


    public RoAuthorizationWebFilter(ReactiveAuthorizationManager<? super ServerWebExchange> accessDecisionManager,
                                    ServerAccessDeniedHandler accessDeniedHandler) {
        this.accessDecisionManager = accessDecisionManager;
        this.accessDeniedHandler = accessDeniedHandler;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication)
                .as(authentication -> this.accessDecisionManager.verify(authentication, exchange))
                .switchIfEmpty(chain.filter(exchange))
                .onErrorResume(AccessDeniedException.class, e -> this.accessDeniedHandler.handle(exchange, e));
    }
}