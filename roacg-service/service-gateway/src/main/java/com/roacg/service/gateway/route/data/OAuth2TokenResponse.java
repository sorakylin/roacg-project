package com.roacg.service.gateway.route.data;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * 作为OAuth2 Client去请求token得到的正确响应
 */
@Data
public class OAuth2TokenResponse {

    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("token_type")
    private String tokenType;

    @JsonAlias("refresh_token")
    private String refreshToken;

    @JsonAlias("expires_in")
    private Long expiresIn;

    private String scope;

    private TokenUserInfo rouser;

    @Data
    public class TokenUserInfo {

        private Long uid;
        private String userName;
        private List<String> userAuthorities;
    }
}
