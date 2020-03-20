package com.roacg.service.gateway.security;

import com.roacg.service.gateway.security.authorization.RoTokenReactiveIntrospector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * https://docs.spring.io/spring-security/site/docs/current/reference/html5
 */

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Autowired
    private OAuth2ResourceServerProperties properties;

    ///https://www.oauth.com/oauth2-servers/token-introspection-endpoint/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin().disable()
                .csrf().disable()
                .authorizeExchange(exchangeSpec -> exchangeSpec
                        .pathMatchers(HttpMethod.POST, "/login", "/logout", "/s/oauth/*").permitAll()
                        .anyExchange().authenticated()
                ).oauth2ResourceServer(oauth -> oauth.opaqueToken(token -> token.introspector(introspector())));

        return http.build();
    }


    @Bean
    public ReactiveOpaqueTokenIntrospector introspector() {
        OAuth2ResourceServerProperties.Opaquetoken opaquetoken = properties.getOpaquetoken();
        return new RoTokenReactiveIntrospector(opaquetoken.getIntrospectionUri(), opaquetoken.getClientId(), opaquetoken.getClientSecret());
    }
}
