package com.roacg.service.gateway.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@AutoConfigureAfter(RedisReactiveAutoConfiguration.class)
public class ReactiveRedisConfig {

    @Bean
    public RedisSerializer<Object> redisSerializer(ObjectMapper objectMapper) {

        //复制一个, 不影响全局
        objectMapper = objectMapper.copy();

        //启用反序列化所需的类型信息,在属性中添加@class
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        //配置null值的序列化器
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, RedisSerializer<Object> redisSerializer) {

        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(redisSerializer)
                .key(StringRedisSerializer.UTF_8)
                .hashKey(StringRedisSerializer.UTF_8)
                .value(redisSerializer)
                .hashValue(redisSerializer)
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
    }
}
