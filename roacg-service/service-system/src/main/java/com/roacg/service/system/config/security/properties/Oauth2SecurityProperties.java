package com.roacg.service.system.config.security.properties;

import com.roacg.core.base.spring.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "oauth2")
@PropertySource(
        value = "classpath:roacg-security.yml",
        factory = YamlPropertySourceFactory.class
)
@Data
public class Oauth2SecurityProperties {

    /**
     * 认证端点设置
     * 用于将 SpringSecurityOAuth2 的默认端点 URL 更改
     */
    @NestedConfigurationProperty
    private Oauth2Endpoint endpoint = new Oauth2Endpoint();

    //是否开启 refreshToken 机制 , 默认 false 不开启
    private Boolean refreshTokenSupport = false;

    //是否开启 refreshToken 重用机制(一个refreshToken可以反复刷新 accessToken)
    private Boolean reuseRefreshToken = false;

    private int refreshTokenValiditySeconds = (int) Duration.ofDays(30).toSeconds(); // default 30 days.

    private int accessTokenValiditySeconds =(int) Duration.ofDays(7).toSeconds(); // default 7 days.

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
        private String tokenKey;

    }

}