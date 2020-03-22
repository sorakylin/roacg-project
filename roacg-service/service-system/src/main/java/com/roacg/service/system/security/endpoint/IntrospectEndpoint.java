package com.roacg.service.system.security.endpoint;

import com.google.common.collect.ImmutableMap;
import com.roacg.service.system.config.security.handler.RoWebResponseExceptionTranslator;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 覆盖掉默认的令牌检查端点 {@link CheckTokenEndpoint}
 * 提供标准的 check token response
 * https://www.oauth.com/oauth2-servers/token-introspection-endpoint/
 */
@FrameworkEndpoint
class IntrospectEndpoint {

    @Resource(type = DefaultTokenServices.class)
    @Lazy
    private ResourceServerTokenServices resourceServerTokenServices;

    private AccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();

    private WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator = new RoWebResponseExceptionTranslator();


    @PostMapping("/oauth/introspect")
    @ResponseBody
    public Map<String, Object> introspect(@RequestParam("token") String value) {

        OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(value);

        if (token == null) {
            return Map.of("active", false, "msg", "Token was not recognised");
        }

        if (token.isExpired()) {
            var builder = ImmutableMap.<String, Object>builder();
            builder.put("active", false).put("msg", "Token has expired");

            if (Objects.nonNull(token.getExpiration())) {
                long exp = token.getExpiration().getTime() / 1000;
                builder.put("exp", exp);
            }

            return builder.build();
        }

        OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(token.getValue());

        Map<String, Object> response = (Map<String, Object>) accessTokenConverter.convertAccessToken(token, authentication);

        // gh-1070
        response.put("active", true);    // Always true if token exists and not expired

        return response;
    }


    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
        return exceptionTranslator.translate(e);
    }
}