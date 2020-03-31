package com.roacg.service.gateway.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.exception.SecurityAuthException;
import com.roacg.core.model.resource.RoApiResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 自定义的认证错误处理器，覆盖 {@link BearerTokenServerAuthenticationEntryPoint}
 * 部分遵循RFC6750规范: https://tools.ietf.org/html/rfc6750#section-3.1
 * 认证错误不返回401/403/200 以外的 HTTP Status
 * <p>
 * Create by skypyb by 2020-03-22
 */
@Component
public class RoTokenServerAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private static Logger logger = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.SECURITY, "gateway-auth");

    private ObjectMapper objectMapper;

    public Function<Object, byte[]> toByteArray = (obj) -> {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    };

    public RoTokenServerAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {

        String path = exchange.getRequest().getPath().contextPath().value();


        return Mono.defer(() -> {
            //实际的Http响应码
            HttpStatus status = getStatus(e);

            //RFC 6750规范
            String wwwAuthenticate = computeWWWAuthenticateHeaderValue(createParameters(e));

            ServerHttpResponse response = exchange.getResponse();

            response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
            response.setStatusCode(status);

            //提取异常信息
            RoApiException exception = extractExceptionAbstract(e);

            logger.info("Authentication exception, Path: {} , Error msg: {}", path, exception.getMsg());

            return response.writeWith(
                    Mono.just(exception)
                            .map(RoApiResponse::fail)
                            .map(toByteArray)
                            .map(bx -> response.bufferFactory().wrap(bx))
            );
        });
    }


    private RoApiException extractExceptionAbstract(AuthenticationException e) {


        if (Objects.isNull(e)) {
            RoApiException unauthorized = new SecurityAuthException();
            unauthorized.setCause(e);
            return unauthorized;
        }

        //如果是OAuth认证异常(Token异常) 就默认使用其信息
        if (e instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) e).getError();
            if (error instanceof BearerTokenError) {

                BearerTokenError bearerTokenError = (BearerTokenError) error;

                HttpStatus httpStatus = bearerTokenError.getHttpStatus();

                RoApiStatusEnum roApiStatusEnum = RoApiStatusEnum.forCode(httpStatus.value()).orElse(RoApiStatusEnum.OTHER);

                RoApiException exception = new RoApiException(roApiStatusEnum, bearerTokenError.getDescription());
                return exception;
            }
        }

        RoApiException unauthorized = new SecurityAuthException();
        unauthorized.setCause(e);

        if (!StringUtils.isEmpty(e.getMessage())) {
            unauthorized.setMsg(e.getMessage());
        }

        return unauthorized;
    }


    private HttpStatus getStatus(AuthenticationException e) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        if (e instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) e).getError();
            if (error instanceof BearerTokenError) {
                status = ((BearerTokenError) error).getHttpStatus();
            }
        }

        //除了401和403，都返回200
        if (status != HttpStatus.UNAUTHORIZED || status != HttpStatus.FORBIDDEN) {
            status = HttpStatus.OK;
        }

        return status;
    }

    private Map<String, String> createParameters(AuthenticationException authException) {
        Map<String, String> parameters = new LinkedHashMap<>();

        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();

            parameters.put("error", error.getErrorCode());

            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }

            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }

            if (error instanceof BearerTokenError) {
                BearerTokenError bearerTokenError = (BearerTokenError) error;

                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
            }
        }
        return parameters;
    }


    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");

        if (parameters.isEmpty()) return wwwAuthenticate.toString();

        wwwAuthenticate.append(" ");
        int i = 0;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            if (i != parameters.size() - 1) {
                wwwAuthenticate.append(", ");
            }
            i++;
        }

        return wwwAuthenticate.toString();
    }
}
