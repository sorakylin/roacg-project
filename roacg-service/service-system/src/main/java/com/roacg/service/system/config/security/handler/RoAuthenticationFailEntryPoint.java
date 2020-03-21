package com.roacg.service.system.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.exception.SecurityAuthException;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.utils.SpringContextUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 有人无凭据就想访问受保护的资源时，用这个处理
 * 返回默认的 json 字符串
 *
 * @author pyb
 * @date 2019/08/31
 */
public class RoAuthenticationFailEntryPoint implements AuthenticationEntryPoint {

    private Logger logger = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.SECURITY, "authentication-fail");

    private ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) {

        RoApiException exception = this.extractExceptionAbstract(e);
        logger.info("认证失败, URI:{} ; err message:{}", request.getRequestURI(), exception.getMessage());

        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(exception.getCode());


        if (Objects.isNull(this.objectMapper)) {
            synchronized (this) {
                if (Objects.isNull(this.objectMapper)) {
                    objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
                }
            }
        }

        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(RoApiResponse.fail(exception)));
    }

    //从SpringSecurity的异常中抽取出信息封装成自己系统的异常
    private RoApiException extractExceptionAbstract(AuthenticationException e) {

        RoApiException unauthorized = null;

        if (e instanceof InternalAuthenticationServiceException) {
            e = (AuthenticationException) e.getCause();
        }

        if (Objects.isNull(e)) return new SecurityAuthException();

        if (e instanceof DisabledException) {
            unauthorized = new SecurityAuthException("User is disabled!");

        } else if (e instanceof BadCredentialsException) {
            unauthorized = new SecurityAuthException("Wrong credentials!");
        } else {
            unauthorized = new SecurityAuthException();

            if (!StringUtils.isEmpty(e.getMessage())) {
                unauthorized.setMsg(e.getMessage());
            }
        }

        unauthorized.setCause(e);
        return unauthorized;
    }
}