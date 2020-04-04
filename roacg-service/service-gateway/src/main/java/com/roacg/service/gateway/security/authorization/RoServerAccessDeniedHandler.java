package com.roacg.service.gateway.security.authorization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.exception.SecurityDeniedException;
import com.roacg.core.model.resource.RoApiResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;

@Component
public class RoServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    private static Logger logger = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.SECURITY, "gateway-auth");

    private ObjectMapper objectMapper;


    public Function<Object, byte[]> toByteArray = (obj) -> {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    };

    public RoServerAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {

        String path = exchange.getRequest().getURI().getRawPath();

        return Mono.defer(() -> {

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);

            logger.info("Access exception, Path: {} , Error msg: {}", path, denied.getMessage());

            //提取异常信息
            RoApiException exception = extractExceptionAbstract(denied);

            return response.writeWith(
                    Mono.just(exception)
                            .map(RoApiResponse::fail)
                            .map(toByteArray)
                            .map(bx -> response.bufferFactory().wrap(bx))
            );
        });
    }

    private RoApiException extractExceptionAbstract(AccessDeniedException e) {

        if (Objects.isNull(e)) return new SecurityDeniedException();

        RoApiException denied = new SecurityDeniedException();

        if (!StringUtils.isEmpty(e.getMessage())) denied.setMsg(e.getMessage());

        denied.setCause(e);
        return denied;
    }
}
