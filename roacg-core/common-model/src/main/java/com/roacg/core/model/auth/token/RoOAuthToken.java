package com.roacg.core.model.auth.token;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内部系统的 Token 信息
 */
@Data
public class RoOAuthToken {

    /**
     * 这些是请求 OAuth2 服务得到的Token 信息
     */
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private Long expiresIn;
    private String scope;
    private TokenUserInfo rouser;
    
    /**
     * 自定义的信息
     */
    private LocalDateTime firstRequestTime;//第一次请求 Token 的时间 (登录)

    private LocalDateTime lastRequestTime;//最后一次请求 Token 的时间 (登录)


    @Data
    public static class TokenUserInfo {

        private Long uid;
        private String userName;
        private List<String> userAuthorities;
    }
}
