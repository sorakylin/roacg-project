package com.roacg.service.tc;

import com.roacg.core.web.security.EnableWebPathExtraction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * TC == Translation collaboration
 * 即翻译协同
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableWebPathExtraction
public class RoacgTcApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoacgTcApplication.class, args);
    }
}
