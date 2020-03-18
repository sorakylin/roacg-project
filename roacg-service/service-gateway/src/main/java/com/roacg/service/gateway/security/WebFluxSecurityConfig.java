package com.roacg.service.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * https://docs.spring.io/spring-security/site/docs/current/reference/html5
 */

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    ///https://www.oauth.com/oauth2-servers/token-introspection-endpoint/
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.formLogin().disable()
                .csrf().disable()
                .authorizeExchange(exchangeSpec -> exchangeSpec
                        .pathMatchers(HttpMethod.POST, "/login", "/logout", "/s/oauth/*").permitAll()
                        .anyExchange().authenticated()
                ).oauth2ResourceServer(oauth -> oauth.opaqueToken());

        return http.build();
    }
}
