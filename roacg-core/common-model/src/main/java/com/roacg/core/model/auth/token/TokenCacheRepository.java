package com.roacg.core.model.auth.token;

public interface TokenCacheRepository<T> {

    void cacheToken(RoOAuthToken token);

    T readTokenCacheByAccessToken(String token);
}
