package com.roacg.service.gateway.route;

import com.roacg.service.gateway.route.handler.LoginHandler;
import com.roacg.service.gateway.route.handler.RegisterHandler;
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
public class PassportRouterStrategyConfig {

    private LoginHandler loginHandler;
    private RegisterHandler registerHandler;


    @Bean
    public RouterFunction<?> routerFunction() {
        return route(POST("/login").and(accept(MediaType.APPLICATION_JSON)), loginHandler)
                .andRoute(POST("/register").and(accept(MediaType.APPLICATION_JSON)), registerHandler)
//                .andRoute(POST("/logout")
//                        .and(accept(MediaType.APPLICATION_FORM_URLENCODED)), loginHandler::handleLogout)
                ;
    }
}
