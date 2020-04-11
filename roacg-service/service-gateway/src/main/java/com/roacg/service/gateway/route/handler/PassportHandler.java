package com.roacg.service.gateway.route.handler;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 登陆、验证、注册、退出
 */
@Component
public class PassportHandler {

    private ReactiveRedisTemplate<String, Object> redisTemplate;
    private WebClient webClient;

    public PassportHandler(ReactiveRedisTemplate<String, Object> redisTemplate, OAuth2ResourceServerProperties properties) {
        this.redisTemplate = redisTemplate;
        this.webClient = WebClient.builder()
                .defaultHeaders(h -> h.setBasicAuth(properties.getOpaquetoken().getClientId(), properties.getOpaquetoken().getClientSecret(), StandardCharsets.UTF_8))
                .build();
    }

    public Mono<ServerResponse> handleLogin(ServerRequest request) {
//        redisTemplate.hasKey("")
//                .filter(Boolean::booleanValue)
//                .map()
        return Mono.empty();
    }

    public Mono<ServerResponse> handleLogout(ServerRequest request) {
        return Mono.empty();
    }

    public Mono<ServerResponse> handleRegister(ServerRequest request) {
        return Mono.empty();
    }
}
