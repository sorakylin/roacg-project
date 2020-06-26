package com.roacg.core.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:dubbo-provider.xml","classpath:dubbo-consumer.xml"})
public class DubboConfig {
}
