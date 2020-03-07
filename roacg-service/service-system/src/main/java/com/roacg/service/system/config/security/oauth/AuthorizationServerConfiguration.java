package com.roacg.service.system.config.security.oauth;

import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.service.system.config.security.properties.Oauth2SecurityProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 声明为 OAuth2 认证服务器
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static Logger preloadLog = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.AT_STARTUP_PRELOAD,"security-oauth2");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Qualifier("authenticationUserService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private Oauth2SecurityProperties oauth2SecurityProperties;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthorizationServerTokenServices tokenServices;



    /**
     * 客户端详情服务
     * 几个重要的属性:
     * clientId：（必须的）用来标识客户的Id。
     * secret：（需要值得信任的客户端）客户端安全码，如果有的话。
     * scope：用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围。
     * authorizedGrantTypes：此客户端可以使用的授权类型，默认为空。
     * authorities：此客户端可以使用的权限（基于Spring Security authorities）。
     *
     * <p> 客户端详情（Client Details）能够在应用程序运行的时候进行更新(访问底层的存储服务)
     * 比如我这里是使用 JdbcClientDetailsService, 就是将客户端详情存储在一个表中
     * 可以自己手动实现 ClientDetailsService 、ClientRegistrationService 接口来对客户端详情进行管理
     *
     * @return JdbcClientDetailsService
     */
    @Bean
    public ClientDetailsService jdbcClientDetails() {
        // 基于 JDBC 实现，需要事先在数据库配置客户端信息
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        return jdbcClientDetailsService;
    }


    /**
     * authenticationManager：认证管理器，当你选择了资源所有者密码（password）授权类型的时候，请设置这个属性注入一个 AuthenticationManager 对象。
     * userDetailsService：设置了这个属性说明有一个自己的 UserDetailsService 接口的实现，或者你可以把这个东西设置到全局域上面去（例如 GlobalAuthenticationManagerConfigurer 这个配置对象）设置了这个之后，"refresh_token" 即刷新令牌授权类型模式的流程中就会包含一个检查，用来确保这个账号是否仍然有效，比如说是否禁用了这个账户。
     * authorizationCodeServices：这个属性是用来设置授权码服务的（即 AuthorizationCodeServices 的实例对象），主要用于 "authorization_code" 授权码类型模式。
     * implicitGrantService：这个属性用于设置隐式授权模式，用来管理隐式授权模式的状态。
     * tokenGranter：这个属性就很牛B了，设置了这个东西（即 TokenGranter 接口实现），那么授权将会交由你来完全掌控，并且会忽略掉上面的这几个属性，这个属性一般是用作拓展用途的，即标准的四种授权模式已经满足不了需求的时候，才会考虑使用这个。
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


        // 用户信息查询服务
        endpoints.userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager);

        //认证端点映射
        this.oauthEndpointUrlSetting(oauth2SecurityProperties.getEndpoint(), endpoints);

        ClientDetailsService jdbcClientDetailsService = this.jdbcClientDetails();

        endpoints.tokenServices(tokenServices);

        // 数据库管理授权码
        endpoints.authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource));
//        endpoints.authorizationCodeServices(new RedisAuthorizationCodeServices(redisConnectionFactory));

        // 数据库管理授权信息
//        ApprovalStore approvalStore = new JdbcApprovalStore(dataSource);

        TokenApprovalStore tokenApprovalStore = new TokenApprovalStore();
        tokenApprovalStore.setTokenStore(tokenStore);
        endpoints.approvalStore(tokenApprovalStore);


//        endpoints.accessTokenConverter(defaultAccessTokenConverter);
    }

    /**
     * /oauth/authorize：授权端点。
     * /oauth/token：令牌端点。
     * /oauth/confirm_access：用户确认授权提交端点。
     * /oauth/error：授权服务错误信息端点。
     * /oauth/check_token：用于资源服务访问的令牌解析端点。
     * /oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
     *
     * <p>注: 授权端点（默认 /oauth/authorize ）这个URL应该被Spring Security保护起来只供授权用户访问
     *
     * @param endpointMapping 端点映射实例
     * @param endpoints       设置端点映射
     */
    private void oauthEndpointUrlSetting(Oauth2SecurityProperties.Oauth2Endpoint endpointMapping,
                                         AuthorizationServerEndpointsConfigurer endpoints) {

        if (Objects.nonNull(endpointMapping.getAuthorize())) {
            endpoints.pathMapping("/oauth/authorize", endpointMapping.getAuthorize());
        }
        if (Objects.nonNull(endpointMapping.getToken())) {
            endpoints.pathMapping("/oauth/token", endpointMapping.getToken());
        }
        if (Objects.nonNull(endpointMapping.getConfirmAccess())) {
            endpoints.pathMapping("/oauth/confirm_access", endpointMapping.getConfirmAccess());
        }
        if (Objects.nonNull(endpointMapping.getError())) {
            endpoints.pathMapping("/oauth/error", endpointMapping.getError());
        }
        if (Objects.nonNull(endpointMapping.getCheckToken())) {
            endpoints.pathMapping("/oauth/check_token", endpointMapping.getCheckToken());
        }
        if (Objects.nonNull(endpointMapping.getTokenKey())) {
            endpoints.pathMapping("/oauth/token_key", endpointMapping.getTokenKey());
        }

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 读取客户端配置
        clients.withClientDetails(jdbcClientDetails());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }
}
