package com.roacg.service.gateway.security;

import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.service.gateway.filter.CorsFilter;
import com.roacg.service.gateway.filter.RoAuthorizationWebFilterProxy;
import com.roacg.service.gateway.security.authentication.RoAuthenticationManager;
import com.roacg.service.gateway.security.authentication.RoTokenReactiveIntrospector;
import com.roacg.service.gateway.security.authentication.RoTokenServerAuthenticationEntryPoint;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.util.FieldUtils;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authorization.DelegatingReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

/**
 * https://docs.spring.io/spring-security/site/docs/current/reference/html5
 */

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class WebFluxSecurityConfig {

    private static Logger preloadLog = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.AT_STARTUP_PRELOAD, "security-web");

    private OAuth2ResourceServerProperties properties;
    private RoTokenServerAuthenticationEntryPoint authEntryPoint;
    private ReactiveAuthorizationManager accessManager;
    private ServerAccessDeniedHandler accessDeniedHandler;


    ///https://www.oauth.com/oauth2-servers/token-introspection-endpoint/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin().disable().csrf().disable();

        http.authorizeExchange(exchangeSpec ->
                exchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().access(accessManager)
        );

        ServerHttpSecurity.AuthorizeExchangeSpec spec = http.authorizeExchange();


        http.addFilterAt(new CorsFilter(), SecurityWebFiltersOrder.CORS)
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(authorizationWebFilter(http.authorizeExchange()), SecurityWebFiltersOrder.AUTHORIZATION);

        preloadLog.info("WebFluxSecurityConfig initialized.");
        return http.build();
    }


    @Bean
    public ReactiveOpaqueTokenIntrospector introspector() {
        OAuth2ResourceServerProperties.Opaquetoken opaquetoken = properties.getOpaquetoken();
        RoTokenReactiveIntrospector result = new RoTokenReactiveIntrospector(opaquetoken.getIntrospectionUri(), opaquetoken.getClientId(), opaquetoken.getClientSecret());
        preloadLog.info("Ro customize token introspector initialized.");

        return result;
    }

    /**
     * 使用自定义的令牌内省 (OAuth2 令牌检查) + 自定义的认证错误处理器来初始化认证过滤器
     *
     * @return 认证过滤器
     */
    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        ReactiveAuthenticationManager authenticationManager = new RoAuthenticationManager(introspector());

        //认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        authenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(authEntryPoint));

        preloadLog.info("Ro customize authentication web filter initialized.");
        return authenticationWebFilter;
    }


    /**
     * 使用自定义的授权过滤器替换掉原有的授权过滤器
     * 这是因为原有的授权过滤器在拒绝请求后没有对异常{@link AccessDeniedException}的处理, 所以自定义静态代理拓展
     * 用反射获取 HttpSecurity 中的 {@link DelegatingReactiveAuthorizationManager.Builder}
     * 这是为了使用到SpringSecurity提供的的访问安全控制机制
     *
     * @param spec SpringSecurity 提供的授权控制器初始化
     * @return 对原有授权过滤器的静态代理
     */
    public RoAuthorizationWebFilterProxy authorizationWebFilter(ServerHttpSecurity.AuthorizeExchangeSpec spec) {
        Object builder = FieldUtils.getProtectedFieldValue("managerBldr", spec);
        DelegatingReactiveAuthorizationManager authorizationManager = ((DelegatingReactiveAuthorizationManager.Builder) builder).build();
        RoAuthorizationWebFilterProxy filter = new RoAuthorizationWebFilterProxy(authorizationManager, accessDeniedHandler);

        preloadLog.info("Ro customize authorization web filter initialized.");

        return filter;
    }
}
