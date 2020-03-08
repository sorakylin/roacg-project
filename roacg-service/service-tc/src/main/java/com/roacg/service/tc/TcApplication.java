package com.roacg.service.tc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TcApplication {
    public static void main(String[] args) {
        SpringApplication.run(TcApplication.class, args);
    }
}
