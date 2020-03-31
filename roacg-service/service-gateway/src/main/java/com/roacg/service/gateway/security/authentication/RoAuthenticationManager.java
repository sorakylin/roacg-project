package com.roacg.service.gateway.security.authentication;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.EXPIRES_AT;
import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.ISSUED_AT;

/**
 * 实现 ReactiveAuthenticationManager 接口来自定义认证接口管理
 * 覆盖默认的 OAuth2 不透明令牌认证管理器{@link OpaqueTokenReactiveAuthenticationManager}
 * Create by skypyb on 2020-03-31
 */
public class RoAuthenticationManager implements ReactiveAuthenticationManager {

    private ReactiveOpaqueTokenIntrospector introspector;

    public RoAuthenticationManager(ReactiveOpaqueTokenIntrospector introspector) {
        Assert.notNull(introspector, "introspector cannot be null");
        this.introspector = introspector;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(a -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap(this::authenticate)
                .cast(Authentication.class);
    }

    /**
     * 作为 OAuth2 Client远程请求 OAuth2 认证服务器
     */
    private Mono<BearerTokenAuthentication> authenticate(String token) {

        //this.tokenStore.readAccessToken(accessToken);

        return this.introspector.introspect(token)
                .map(principal -> {
                    Instant iat = principal.getAttribute(ISSUED_AT);
                    Instant exp = principal.getAttribute(EXPIRES_AT);

                    // construct token
                    OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, iat, exp);
                    return new BearerTokenAuthentication(principal, accessToken, principal.getAuthorities());
                })
                .onErrorMap(OAuth2IntrospectionException.class, this::onError);
    }


    private AuthenticationException onError(OAuth2IntrospectionException e) {
        if (e instanceof BadOpaqueTokenException) {
            return new InvalidBearerTokenException(e.getMessage(), e);
        } else {
            return new AuthenticationServiceException(e.getMessage(), e);
        }
    }
}