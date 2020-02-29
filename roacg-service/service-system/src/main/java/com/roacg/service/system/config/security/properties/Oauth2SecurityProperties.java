package com.roacg.service.system.config.security.properties;

import com.roacg.core.base.spring.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
@PropertySource(
        value = "classpath:roacg-security.yml",
        factory = YamlPropertySourceFactory.class
)
public class Oauth2SecurityProperties {


    /**
     * 认证端点设置
     * 用于将 SpringSecurityOAuth2 的默认端点 URL 更改
     */
    @NestedConfigurationProperty
    private Oauth2Endpoint endpoint = new Oauth2Endpoint();

    //是否开启 refreshToken 机制 , 默认 false 不开启
    private Boolean refreshTokenSupport = false;

    private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30; // default 30 days.

    private int accessTokenValiditySeconds = 60 * 60 * 12; // default 12 hours.

    public Oauth2Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Oauth2Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Boolean getRefreshTokenSupport() {
        return refreshTokenSupport;
    }

    public void setRefreshTokenSupport(Boolean refreshTokenSupport) {
        this.refreshTokenSupport = refreshTokenSupport;
    }

    public int getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

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

        public String getAuthorize() {
            return authorize;
        }

        public void setAuthorize(String authorize) {
            this.authorize = authorize;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getConfirmAccess() {
            return confirmAccess;
        }

        public void setConfirmAccess(String confirmAccess) {
            this.confirmAccess = confirmAccess;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getCheckToken() {
            return checkToken;
        }

        public void setCheckToken(String checkToken) {
            this.checkToken = checkToken;
        }

        public String getTokenKey() {
            return tokenKey;
        }

        public void setTokenKey(String tokenKey) {
            this.tokenKey = tokenKey;
        }
    }

}