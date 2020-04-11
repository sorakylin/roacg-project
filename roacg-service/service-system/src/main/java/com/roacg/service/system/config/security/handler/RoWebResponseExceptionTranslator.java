package com.roacg.service.system.config.security.handler;

import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.service.system.security.exception.ROAuth2Exception;
import com.roacg.service.system.security.exception.ROAuth2ExceptionSerializer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * OAuth2 端点的异常处理
 * 代理默认的实现, 做出额外处理。
 *
 * @see ROAuth2ExceptionSerializer 序列化返回的 JSON
 */
public class RoWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    private Logger logger = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.SECURITY, "oauth2-endpoint");

    private DefaultWebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Override
    @SneakyThrows
    public ResponseEntity<OAuth2Exception> translate(Exception e) {

        var responseEntity = exceptionTranslator.translate(e);
        var ase = responseEntity.getBody();

        var httpReq = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //客户端异常直接返回? 解析问题
        /*if (ase instanceof ClientAuthenticationException) {
            return responseEntity;
        }*/

        HttpHeaders headers = responseEntity.getHeaders();
        HttpStatus status = responseEntity.getStatusCode();


        logger.info("OAuth2 exception, URI: {}, Code:{} ,Summary: {}.", httpReq.getRequestURI(), status.value(), ase.getSummary());


        //如果不是 401|403 则响应200
        if (status != HttpStatus.UNAUTHORIZED && status != HttpStatus.FORBIDDEN) {
            status = HttpStatus.OK;
        }

        return ResponseEntity.status(status)
                .headers(headers)
                .body(new ROAuth2Exception(ase));
    }


}