package com.roacg.service.gateway.security.repository;

import com.roacg.core.model.auth.token.RoOAuthToken;
import com.roacg.core.model.auth.token.TokenCacheRepository;
import com.roacg.core.model.consts.RoCacheConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Repository
public class ReactiveRedisTokenRepository implements TokenCacheRepository<Mono<RoOAuthToken>> {

    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;

    @Override
    public void cacheToken(RoOAuthToken token) {

        LocalDateTime firstTime = token.getFirstRequestTime();

        Duration expTime = Duration.between(firstTime, firstTime.plusSeconds(token.getExpiresIn()));

        String tokenCacheKey = RoCacheConst.getTokenCacheKey(token.getAccessToken());
        String userToTokenKey = RoCacheConst.getUserToTokenKey(token.getRouser().getUid(), token.getRouser().getClientId());

        ReactiveValueOperations<String, Object> strOpt = reactiveRedisTemplate.opsForValue();

        strOpt.set(tokenCacheKey, token, expTime)
                .filter(Boolean.TRUE::equals)
                .map(b -> reactiveRedisTemplate.opsForSet().add(userToTokenKey, token.getAccessToken()).subscribe())
                .subscribe();
    }

    @Override
    public Mono<RoOAuthToken> readTokenCacheByAccessToken(String token) {
        ReactiveValueOperations<String, RoOAuthToken> strOpt = reactiveRedisTemplate.opsForValue();
        String tokenCacheKey = RoCacheConst.getTokenCacheKey(token);

        return strOpt.get(tokenCacheKey);
    }
}
