package com.roacg.core.web.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.roacg.service.**.service")
@PropertySource("classpath:/dubbo.properties")
public class DubboConfig {
}
