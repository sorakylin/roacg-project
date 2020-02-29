package com.roacg.service.system.config.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties("oauth2")
@PropertySource(value = {"classpath:security.yml"})
@Data
public class Oauth2SecurityProperties {

    /**
     * 认证端点设置
     * 用于将 SpringSecurityOAuth2 的默认端点 URL 更改
     */
    private Oauth2Endpoint endpoint = new Oauth2Endpoint();

    //是否开启 refreshToken 机制 , 默认 false 不开启
    private boolean refreshTokenSupport = false;

    @Data
    public class Oauth2Endpoint {

        //授权端点 /oauth/authorize
        private String authorize;

        //令牌端点 /oauth/token
        private String token;

        //用户确认授权提交端点 /oauth/confirm_access
        private String confirmAccess;

        //授权服务错误信息端点 /oauth/error
        private String error;

        //用于资源服务访问的令牌解析端点 /oauth/check_token
        private String checkToken;

        //提供公有密匙的端点，如果使用JWT令牌的话 /oauth/token_key
        private String tokenkey;

    }

}