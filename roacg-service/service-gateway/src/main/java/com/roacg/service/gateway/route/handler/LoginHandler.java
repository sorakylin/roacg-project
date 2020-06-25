package com.roacg.service.gateway.route.handler;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.roacg.core.model.auth.token.RoOAuthToken;
import com.roacg.core.model.auth.token.TokenCacheRepository;
import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.utils.JsonUtil;
import com.roacg.service.gateway.route.data.LoginRequest;
import com.roacg.service.gateway.route.data.OAuth2TokenResponse;
import com.roacg.service.gateway.security.GatewaySecurityProperties;
import org.springframework.beans.factory.InitializingBean;
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

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 登陆服务
 */
@Component
public class LoginHandler implements HandlerFunction<ServerResponse>, InitializingBean {

    private ReactiveRedisTemplate<String, Object> redisTemplate;
    private GatewaySecurityProperties properties;
    private TokenCacheRepository<Mono<RoOAuthToken>> tokenRepository;
    private WebClient webClient;

    public LoginHandler(ReactiveRedisTemplate<String, Object> redisTemplate,
                        GatewaySecurityProperties properties,
                        @Nullable TokenCacheRepository<Mono<RoOAuthToken>> tokenRepository) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.webClient = WebClient.builder()
                .defaultHeaders(h -> h.setBasicAuth(properties.getClientId(), properties.getClientSecret(), StandardCharsets.UTF_8))
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
                .flatMap(token -> this.successResponse(token))
                .onErrorResume(RoApiException.class, this::onError);
    }


    //封装请求体
    private Mono<ClientResponse> makeRequest(LoginRequest req) {
        return this.webClient.post()
                .uri(properties.getEndpoint().getOauthToken())
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
    private Mono<ServerResponse> successResponse(OAuth2TokenResponse token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Set-Token", token.getAccessToken());

        //啥时候过期, 毫秒值
        long expire = Instant.now().plus(Duration.ofSeconds(token.getExpiresIn())).toEpochMilli();
        headers.set("Set-Token-Expire", String.valueOf(expire));

        Map<String, Object> body = Map.of(
                "uid", token.getRouser().getUid(),
                "userName", token.getRouser().getUserName()
        );

        return ServerResponse.ok().headers(h -> h.addAll(headers)).bodyValue(RoApiResponse.ok(body));
    }

    //登陆成功后, 将Token缓存到Redis
    private void cacheToken(OAuth2TokenResponse token) {
        if (Objects.isNull(tokenRepository)) return;

        LocalDateTime now = LocalDateTime.now();

        //如果Redis里已经有了的话, 其实这次获取的也是一模一样的
        tokenRepository.readTokenCacheByAccessToken(token.getAccessToken())
                .defaultIfEmpty(token.generateSaveToken())
                .doOnNext(tk -> {
                    if (Objects.isNull(tk.getLastRequestTime())) {
                        tk.setFirstRequestTime(now);
                    }
                    tk.setLastRequestTime(now);
                }).subscribe(tokenRepository::cacheToken);
    }

    //对于失败响应的处理 (只处理RoApiException)  响应合适的状态
    private Mono<ServerResponse> onError(RoApiException e) {

        int code = e.getCode();
        if (code != 401 && code != 403) code = 200;

        return ServerResponse.status(code).body(BodyInserters.fromValue(RoApiResponse.fail(e)));
    }
}
