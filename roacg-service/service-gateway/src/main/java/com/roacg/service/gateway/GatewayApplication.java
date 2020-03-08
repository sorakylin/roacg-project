package com.roacg.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关层本身需要是一个 Oauth2 Resource Server
 * 需要做对资源的鉴权行为
 * 支持 ajax 登陆(传入用户名密码、返回accessToken )
 *
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {

        SpringApplication.run(GatewayApplication.class, args);
    }
}
