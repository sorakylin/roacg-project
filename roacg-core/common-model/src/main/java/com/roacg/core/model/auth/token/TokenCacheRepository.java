package com.roacg.core.model.auth.token;

import java.util.Optional;

public interface TokenCacheRepository {

    void cacheToken(RoOAuthToken token);

    Optional<RoOAuthToken> readTokenCacheByAccessToken(String token);
}
