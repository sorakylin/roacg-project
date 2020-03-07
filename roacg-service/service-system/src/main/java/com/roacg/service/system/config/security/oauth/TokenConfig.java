package com.roacg.service.system.config.security.oauth;

import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.service.system.config.security.properties.Oauth2SecurityProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@Configuration
public class TokenConfig {

    private static Logger preloadLog = RoLoggerFactory
            .getCommonLogger(RoCommonLoggerEnum.AT_STARTUP_PRELOAD, "security-oauth2");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Oauth2SecurityProperties oauth2SecurityProperties;


    /**
     * 持久化令牌是委托一个 TokenStore 接口来实现
     * TokenStore 这个接口有一个默认的实现，就是 InMemoryTokenStore ，所有的令牌是被保存在了内存中。
     *
     * @return JdbcTokenStore
     */
    @Bean
    public TokenStore tokenStore() {

        // Redis管理access_token和refresh_token
//        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);

        // JBDC管理access_token和refresh_token

        // 基于 JDBC 实现，令牌保存到数据库
        JdbcTokenStore jdbcTokenStore = new JdbcTokenStore(dataSource);
        return jdbcTokenStore;
    }

    /**
     * 授权令牌服务
     * 主要是对于 token 的创建、刷新等流程进行管控
     *
     * @return jdbc service
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices(ClientDetailsService clientDetailsService) {

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setClientDetailsService(clientDetailsService);

        preloadLog.info("Refresh token support enable: {}.", oauth2SecurityProperties.getRefreshTokenSupport());
        preloadLog.info("Refresh token validity seconds: {}.", oauth2SecurityProperties.getRefreshTokenValiditySeconds());
        preloadLog.info("Access token validity seconds: {}.", oauth2SecurityProperties.getAccessTokenValiditySeconds());

        //设置token相关信息
        tokenServices.setSupportRefreshToken(oauth2SecurityProperties.getRefreshTokenSupport());
        tokenServices.setAccessTokenValiditySeconds(oauth2SecurityProperties.getAccessTokenValiditySeconds());
        tokenServices.setRefreshTokenValiditySeconds(oauth2SecurityProperties.getRefreshTokenValiditySeconds());


//        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
//        defaultAccessTokenConverter.setUserTokenConverter(new CustomUserAuthenticationConverter());
//

        return tokenServices;
    }

}
