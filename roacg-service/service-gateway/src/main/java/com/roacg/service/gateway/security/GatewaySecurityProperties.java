package com.roacg.service.gateway.security;

import com.roacg.core.base.spring.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
@PropertySource(
        value = "classpath:gateway-security.yml",
        factory = YamlPropertySourceFactory.class
)
@Data
public class GatewaySecurityProperties {

    private String clientId;

    private String clientSecret;

    @NestedConfigurationProperty
    private Endpoint endpoint = new Endpoint();

    //令牌端点
    @Data
    public class Endpoint {
        //token认证
        private String oauthToken;
        //检查token
        private String checkToken;
    }
}

