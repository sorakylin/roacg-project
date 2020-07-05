package com.roacg.core.web.config;

import com.roacg.core.web.config.convert.IntegerCodeToEnumConverterFactory;
import com.roacg.core.web.config.convert.StringCodeToEnumConverterFactory;
import com.roacg.core.web.interceptor.GlobalExceptionHandler;
import com.roacg.core.web.interceptor.RequestContextInterceptor;
import com.roacg.core.web.security.repository.RedisTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ConditionalOnWebApplication
public class WebAppConfigurer implements WebMvcConfigurer {

    @Autowired
    private RedisTokenRepository tokenRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new RequestContextInterceptor(tokenRepository)).addPathPatterns("/**");
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IntegerCodeToEnumConverterFactory());
        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {

        //自定义异常处理器放在首位
        resolvers.add(0, new GlobalExceptionHandler());
    }
}