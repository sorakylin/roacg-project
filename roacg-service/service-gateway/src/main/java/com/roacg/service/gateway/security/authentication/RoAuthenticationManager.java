package com.roacg.service.gateway.security.authentication;

import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.core.model.auth.CredentialsType;
import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.auth.token.RoOAuthToken;
import com.roacg.core.model.auth.token.TokenCacheRepository;
import com.roacg.core.model.consts.RoAuthConst;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.*;

/**
 * 实现 ReactiveAuthenticationManager 接口来自定义认证接口管理
 * 覆盖默认的 OAuth2 不透明令牌认证管理器{@link OpaqueTokenReactiveAuthenticationManager}
 * Create by skypyb on 2020-03-31
 */
public class RoAuthenticationManager implements ReactiveAuthenticationManager {

    private Logger logger = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.SECURITY, "Authentication");

    private ReactiveOpaqueTokenIntrospector introspector;

    private TokenCacheRepository<Mono<RoOAuthToken>> tokenCacheRepository;

    public RoAuthenticationManager(ReactiveOpaqueTokenIntrospector introspector) {
        Assert.notNull(introspector, "introspector cannot be null");
        this.introspector = introspector;
    }

    public RoAuthenticationManager(ReactiveOpaqueTokenIntrospector introspector, TokenCacheRepository<Mono<RoOAuthToken>> tokenCacheRepository) {
        Assert.notNull(introspector, "introspector cannot be null");
        this.introspector = introspector;
        this.tokenCacheRepository = tokenCacheRepository;
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

        return this.readCache(token)
                .switchIfEmpty(introspector.introspect(token))
                .map(principal -> {
                    Instant iat = principal.getAttribute(ISSUED_AT);
                    Instant exp = principal.getAttribute(EXPIRES_AT);

                    // construct token
                    OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, iat, exp);
                    return new BearerTokenAuthentication(principal, accessToken, principal.getAuthorities());
                })
                .onErrorMap(OAuth2IntrospectionException.class, this::onError);
    }

    private Mono<OAuth2AuthenticatedPrincipal> readCache(String token) {
        if (tokenCacheRepository == null) return Mono.empty();


        return tokenCacheRepository.readTokenCacheByAccessToken(token)
                .map(tokenInfo -> {
//                    logger.info("Read cache success, token:{}", tokenInfo);
                    //用户的权限
                    List<GrantedAuthority> authorities = tokenInfo.getRouser().getUserAuthorities()
                            .stream()
                            .map("SCOPE_"::concat)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    Map<String, Object> claims = new HashMap<>();
                    claims.put(SCOPE, tokenInfo.getScope());

                    Instant exp = tokenInfo.getFirstRequestTime()
                            .plusSeconds(tokenInfo.getExpiresIn())
                            .toInstant(OffsetDateTime.now().getOffset());
                    claims.put(EXPIRES_AT, exp);

                    RoOAuthToken.TokenUserInfo user = tokenInfo.getRouser();
                    RequestUser requestUser = RequestUser.builder()
                            .credentials(token)
                            .credentialsType(CredentialsType.TOKEN)
                            .id(user.getUid())
                            .clientId(user.getClientId())
                            .name(user.getUserName())
                            .authorities(user.getUserAuthorities())
                            .build();
                    claims.put(RoAuthConst.TOKEN_USER_KEY, requestUser);

                    OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(claims, authorities);
                    return principal;
                });
    }

    private AuthenticationException onError(OAuth2IntrospectionException e) {
        if (e instanceof BadOpaqueTokenException) {
            return new InvalidBearerTokenException(e.getMessage(), e);
        } else {
            return new AuthenticationServiceException(e.getMessage(), e);
        }
    }
}