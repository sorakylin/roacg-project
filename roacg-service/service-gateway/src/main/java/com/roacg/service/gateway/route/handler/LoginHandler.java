package com.roacg.service.gateway.route.handler;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.utils.JsonUtil;
import com.roacg.service.gateway.route.data.LoginRequest;
import com.roacg.service.gateway.route.data.OAuth2TokenResponse;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * 登陆服务
 */
@Component
public class LoginHandler implements HandlerFunction<ServerResponse> {

    private ReactiveRedisTemplate<String, Object> redisTemplate;
    private WebClient webClient;

    public LoginHandler(ReactiveRedisTemplate<String, Object> redisTemplate, OAuth2ResourceServerProperties properties) {
        this.redisTemplate = redisTemplate;
        this.webClient = WebClient.builder()
                .defaultHeaders(h -> h.setBasicAuth(properties.getOpaquetoken().getClientId(), properties.getOpaquetoken().getClientSecret(), StandardCharsets.UTF_8))
                .build();
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {

        return request.bodyToMono(LoginRequest.class)
                .flatMap(this::makeRequest)
                .flatMap(this::extractResponse)
                .doOnNext(this::validateResponse)
                .map(res -> ((OAuth2TokenResponse) res.getData()))
                .doOnNext(this::cacheToken)
                .flatMap(token -> this.successResponse(token.getAccessToken(), token.getExpiresIn()))
                .onErrorResume(RoApiException.class, this::onError);
    }


    //封装请求体
    private Mono<ClientResponse> makeRequest(LoginRequest req) {
        return this.webClient.post()
                .uri("http://localhost:7080/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", req.getUsername())
                        .with("password", req.getPassword()))
                .exchange();
    }

    //将http响应抽取为自定义的对象, 成功了的话会包含一个 OAuth2TokenResponse 实体
    private Mono<RoApiResponse> extractResponse(ClientResponse responseEntity) {

        //先看状态码
        if (responseEntity.rawStatusCode() != HTTPResponse.SC_OK) {
            return responseEntity.bodyToMono(RoApiResponse.class);
        }

        //返回对应的对象
        return responseEntity.bodyToMono(String.class)
                .map(body -> {
                    if (StringUtils.hasText(body) && body.contains("access_token")) {
                        return JsonUtil
                                .fromJsonToObject(body, OAuth2TokenResponse.class)
                                .map(RoApiResponse::ok)
                                .get();
                    } else {
                        return JsonUtil.fromJsonToObject(body, RoApiResponse.class).get();
                    }
                });
    }

    //效验响应正确与否, 不正确就报错
    private void validateResponse(RoApiResponse response) {
        if (response.getSuccess()) return;

        RoApiStatusEnum status = RoApiStatusEnum.forCode(response.getCode()).get();
        throw new RoApiException(status, response.getMsg(), response.getData());
    }

    //成功的http响应
    private Mono<ServerResponse> successResponse(String accessToken, Long expiresIn) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Set-Token", accessToken);

        //啥时候过期, 毫秒值
        long expire = Instant.now().plus(Duration.ofSeconds(expiresIn)).toEpochMilli();
        headers.set("Set-Token-Expire", String.valueOf(expire));


        return ServerResponse.ok().headers(h -> h.addAll(headers)).build();
    }

    private void cacheToken(OAuth2TokenResponse oAuth2TokenResponse) {

    }

    //对于失败响应的处理 (只处理RoApiException)  响应合适的状态
    private Mono<ServerResponse> onError(RoApiException e) {

        int code = e.getCode();
        if (code != 401 && code != 403) code = 200;

        return ServerResponse.status(code).body(BodyInserters.fromValue(RoApiResponse.fail(e)));
    }
}
