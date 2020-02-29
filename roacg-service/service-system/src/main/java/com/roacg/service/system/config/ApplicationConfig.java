package com.roacg.service.system.config;

import com.roacg.service.system.config.security.properties.Oauth2SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({Oauth2SecurityProperties.class})
public class ApplicationConfig {

}
