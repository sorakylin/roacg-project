package com.roacg.service.system.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.exception.SecurityAuthException;
import com.roacg.core.model.exception.SecurityDeniedException;
import com.roacg.core.model.resource.RoApiResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 授权拒绝处理器，覆盖默认的 OAuth2AccessDeniedHandler
 */
@Component
public class RoOAuth2AccessDeniedHandler extends OAuth2AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    private Logger logger = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.SECURITY, "oauth2-access");


    public RoOAuth2AccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * 授权拒绝处理, 返回自定义JSON
     *
     * @param request  request
     * @param response response
     * @param e        authException
     */
    @Override
    @SneakyThrows
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {

        RoApiException exception = this.extractExceptionAbstract(e);
        logger.info("授权失败，禁止访问URI: {}; err message:{}", request.getRequestURI(), exception.getMessage());

        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(exception.getCode());


        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(RoApiResponse.fail(exception)));
    }

    private RoApiException extractExceptionAbstract(AccessDeniedException e) {

        if (Objects.isNull(e)) return new SecurityDeniedException();

        RoApiException denied = new SecurityAuthException();

        if (!StringUtils.isEmpty(e.getMessage())) denied.setMsg(e.getMessage());

        denied.setCause(e);
        return denied;
    }
}
