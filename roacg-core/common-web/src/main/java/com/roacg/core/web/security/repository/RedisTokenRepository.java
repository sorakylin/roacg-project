package com.roacg.core.web.security.repository;

import com.roacg.core.model.auth.token.RoOAuthToken;
import com.roacg.core.model.auth.token.TokenCacheRepository;
import com.roacg.core.model.consts.RoCacheConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class RedisTokenRepository implements TokenCacheRepository<Optional<RoOAuthToken>> {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void cacheToken(RoOAuthToken token) {

        LocalDateTime firstTime = token.getFirstRequestTime();
        Duration expTime = Duration.between(firstTime, firstTime.plusSeconds(token.getExpiresIn()));

        String tokenCacheKey = RoCacheConst.getTokenCacheKey(token.getAccessToken());
        String userToTokenKey = RoCacheConst.getUserToTokenKey(token.getRouser().getUid(), token.getRouser().getClientId());

        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.opsForValue().set(tokenCacheKey, token, expTime);
                operations.opsForSet().add(userToTokenKey, token.getAccessToken());
                return null;
            }
        });

    }

    @Override
    public Optional<RoOAuthToken> readTokenCacheByAccessToken(String token) {
        ValueOperations<String, RoOAuthToken> valOpt = redisTemplate.opsForValue();

        String tokenCacheKey = RoCacheConst.getTokenCacheKey(token);
        return Optional.ofNullable(valOpt.get(tokenCacheKey));
    }
}
