package com.roacg.service.gateway.route;

import com.roacg.service.gateway.route.handler.PassportHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
@AllArgsConstructor
public class RouterStrategyConfig {

    private PassportHandler passportHandler;


    @Bean
    public RouterFunction<?> routerFunction() {
        return route(POST("/login").and(accept(MediaType.APPLICATION_JSON)), passportHandler::handleLogin)
                .andRoute(POST("/register")
                        .and(accept(MediaType.APPLICATION_JSON)), passportHandler::handleRegister)
                .andRoute(POST("/logout")
                        .and(accept(MediaType.APPLICATION_FORM_URLENCODED)), passportHandler::handleLogout)

                ;
    }
}
