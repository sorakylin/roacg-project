package com.roacg.service.gateway.route.data;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.roacg.core.model.auth.token.RoOAuthToken;
import lombok.Data;

/**
 * 作为OAuth2 Client去请求token得到的正确响应
 */
@Data
public class OAuth2TokenResponse extends RoOAuthToken {

    @Override
    @JsonAlias("access_token")
    public void setAccessToken(String accessToken) {
        super.setAccessToken(accessToken);
    }

    @Override
    @JsonAlias("token_type")
    public void setTokenType(String tokenType) {
        super.setTokenType(tokenType);
    }

    @Override
    @JsonAlias("refresh_token")
    public void setRefreshToken(String refreshToken) {
        super.setRefreshToken(refreshToken);
    }

    @Override
    @JsonAlias("expires_in")
    public void setExpiresIn(Long expiresIn) {
        super.setExpiresIn(expiresIn);
    }
}
