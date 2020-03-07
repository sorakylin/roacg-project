package com.roacg.service.system.config.cache;

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

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
class RedisCacheConfig extends CachingConfigurerSupport {

    /**
     * 默认的缓存key前缀
     */
    public static final String DEFAULT_KEY_PREFIX = "RO:DEF-KEY@";

    @Bean
    public CacheManager cacheManager(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory,
                                     RedisSerializer<Object> redisSerializer) {

        return RedisCacheManager.builder(factory)
                .cacheDefaults(getRedisCacheConfigurationWithTtl(60, redisSerializer))
                .build();
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer minutes, RedisSerializer<Object> redisSerializer) {

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .prefixKeysWith(DEFAULT_KEY_PREFIX)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .entryTtl(Duration.ofMinutes(minutes));

        return cacheConfig;
    }

    @Override
    public KeyGenerator keyGenerator() {
        // 当没有指定缓存的 key时来根据类名、方法名和方法参数来生成key
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder();
            key.append(target.getClass().getName())
                    .append(':')
                    .append(method.getName());

            String paramsStringValue = Arrays.stream(params).map(param -> {
                //null参数设置
                if (Objects.isNull(param)) {
                    param = "_NULL";
                }
                return param.toString();
            }).collect(Collectors.joining(",", "[", "]"));

            return key.append(paramsStringValue).toString();
        };
    }
}