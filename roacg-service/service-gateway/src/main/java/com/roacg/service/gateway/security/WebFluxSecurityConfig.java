package com.roacg.service.gateway.security;

import com.roacg.service.gateway.filter.CorsFilter;
import com.roacg.service.gateway.filter.RoAuthorizationWebFilter;
import com.roacg.service.gateway.security.authentication.RoAuthenticationManager;
import com.roacg.service.gateway.security.authentication.RoTokenReactiveIntrospector;
import com.roacg.service.gateway.security.authentication.RoTokenServerAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

/**
 * https://docs.spring.io/spring-security/site/docs/current/reference/html5
 */

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Autowired
    private OAuth2ResourceServerProperties properties;

    @Autowired
    private RoTokenServerAuthenticationEntryPoint authEntryPoint;

    @Autowired
    private ReactiveAuthorizationManager accessManager;

    @Autowired
    private ServerAccessDeniedHandler accessDeniedHandler;

    ///https://www.oauth.com/oauth2-servers/token-introspection-endpoint/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin().disable().csrf().disable();

        http.authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll());


        http.addFilterAt(new CorsFilter(), SecurityWebFiltersOrder.CORS)
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(authorizationWebFilter(), SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();
    }


    @Bean
    public ReactiveOpaqueTokenIntrospector introspector() {
        OAuth2ResourceServerProperties.Opaquetoken opaquetoken = properties.getOpaquetoken();
        return new RoTokenReactiveIntrospector(opaquetoken.getIntrospectionUri(), opaquetoken.getClientId(), opaquetoken.getClientSecret());
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        ReactiveAuthenticationManager authenticationManager = new RoAuthenticationManager(introspector());

        //认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        authenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(authEntryPoint));


        return authenticationWebFilter;
    }

    @Bean
    public RoAuthorizationWebFilter authorizationWebFilter() {
        RoAuthorizationWebFilter filter = new RoAuthorizationWebFilter(accessManager, accessDeniedHandler);
        return filter;
    }
}
