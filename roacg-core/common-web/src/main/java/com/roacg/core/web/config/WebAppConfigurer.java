package com.roacg.core.web.config;

import com.roacg.core.web.security.interceptor.RequestContextInterceptor;
import com.roacg.core.web.security.repository.RedisTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Autowired
    private RedisTokenRepository tokenRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new RequestContextInterceptor(tokenRepository)).addPathPatterns("/**");
    }

}