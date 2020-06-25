package com.roacg.service.gateway.security.repository;

import com.roacg.core.model.auth.token.RoOAuthToken;
import com.roacg.core.model.auth.token.TokenCacheRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReactiveRedisTokenRepository implements TokenCacheRepository {

    @Override
    public void cacheToken(RoOAuthToken token) {

    }

    @Override
    public Optional<RoOAuthToken> readTokenCacheByAccessToken(String token) {
        return Optional.empty();
    }
}
