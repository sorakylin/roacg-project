package com.roacg.core.web.config;

import com.roacg.core.model.consts.RoCacheConst;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
class RedisCacheConfig extends CachingConfigurerSupport {


    @Bean
    public CacheManager cacheManager(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory,
                                     RedisSerializer<Object> redisSerializer) {

        return RedisCacheManager.builder(factory)
                .cacheDefaults(getRedisCacheConfigurationWithTtl(redisSerializer))
                .build();
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(RedisSerializer<Object> redisSerializer) {

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .prefixKeysWith(RoCacheConst.DEFAULT_KEY_PREFIX)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .entryTtl(RoCacheConst.DEFAULT_TTL);

        return cacheConfig;
    }

    @Override
    public KeyGenerator keyGenerator() {
        // 当没有指定缓存的 key时来根据类名、方法名和方法参数来生成key
        return (target, method, params) -> RoCacheConst.defaultKeyGenerate(target, method, params);
    }
}