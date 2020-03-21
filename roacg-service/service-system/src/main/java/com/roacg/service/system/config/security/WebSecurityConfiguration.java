package com.roacg.service.system.config.security;

import com.roacg.core.base.log.RoCommonLoggerEnum;
import com.roacg.core.base.log.RoLoggerFactory;
import com.roacg.service.system.config.security.handler.RoAuthenticationFailEntryPoint;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static Logger preloadLog = RoLoggerFactory.getCommonLogger(RoCommonLoggerEnum.AT_STARTUP_PRELOAD, "security-web");

    @Qualifier("authenticationUserService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 设置默认的加密方式
        return new BCryptPasswordEncoder();
    }

    private AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());

        //BadCredentialsException --> UsernameNotFoundException
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .authenticationProvider(this.authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //默认所有请求都需要认证
        http.authorizeRequests(authReq -> authReq.anyRequest().authenticated())
                .csrf().disable()//禁用CSRF
                .formLogin().disable()//禁用form登录
                .sessionManagement(se -> se.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  //禁用session
                .exceptionHandling(eh -> eh.authenticationEntryPoint(new RoAuthenticationFailEntryPoint()));//异常处理
    }


    /**
     * AuthenticationManager:
     * 用户认证的管理类，所有的认证请求（比如login）都会通过提交一个 token 给 AuthenticationManager 的 authenticate() 方法来实现。
     * 具体校验动作会由 AuthenticationManager 将请求转发给具体的实现类来做。根据实现反馈的结果再调用具体的 Handler 来给用户以反馈。
     *
     * @return default AuthenticationManager
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //跨域设置
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //跨域请求访问配置
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");//请求方法限制:如GET/POST，*表示所有都允许
        //configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");//容许任何来源的跨域访问 FIXME 开发调试用
//        configuration.addExposedHeader("Authorization");
        //对当前这个服务器下所有有的请求都启用这个配置
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }

}
