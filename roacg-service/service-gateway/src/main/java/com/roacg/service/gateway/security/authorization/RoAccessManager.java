package com.roacg.service.gateway.security.authorization;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.consts.RoAuthConst;
import com.roacg.service.gateway.security.PermissionVerifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义访问控制，所有权限相关判断均在此进行
 * Create by skypyb on 2020-03-31
 */
@Component
public class RoAccessManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    /**
     * 实现权限验证判断,只有两种情况下会进来判定
     * 1、用户已登录, 携带正确的 token 请求资源
     * 2、用户未登录, 未携带 token
     * 如果用户没有携带 Token的话 Mono<Authentication> 包含的认证会为 null
     * <p>
     * 若是用户携带了错误的 token (比如过期了)  它就压根不会进来判断权限, 而是在认证处就被拦截掉了。
     * 所以对于需要忽略的路径而言，需要在认证时{@link ReactiveAuthenticationManager}就将路径忽略
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authenticationMono, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();

        //当前试图请求的资源
        String requestPath = exchange.getRequest().getURI().getRawPath();
        HttpMethod method = exchange.getRequest().getMethod();

        return authenticationMono
                .map(auth -> auth.getPrincipal())
                .cast(OAuth2AuthenticatedPrincipal.class)
                .map(auth -> auth.getAttribute(RoAuthConst.TOKEN_USER_KEY))
                .cast(RequestUser.class)
                .defaultIfEmpty(RequestUser.guest())
                .map(user -> new AuthorizationDecision(PermissionVerifier.checkResource(user, requestPath, method)));
    }

}
