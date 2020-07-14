package com.roacg.core.web.config;

import com.roacg.core.utils.serialize.LocalDateSerializer;
import com.roacg.core.utils.serialize.LocalDateTimeSerializer;
import com.roacg.core.utils.serialize.LongAccuracyCompatibleSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class JacksonConfig {


    @Bean("jackson2ObjectMapperBuilderCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Jackson2ObjectMapperBuilderCustomizer customizer = (builder) -> builder
                .serializerByType(Long.TYPE, LongAccuracyCompatibleSerializer.INSTANCE)
                .serializerByType(LocalDate.class, LocalDateSerializer.INSTANCE)
                .serializerByType(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        return customizer;
    }
}
