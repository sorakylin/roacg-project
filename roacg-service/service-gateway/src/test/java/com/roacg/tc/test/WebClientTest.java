package com.roacg.tc.test;


import com.roacg.service.gateway.security.authentication.RoTokenReactiveIntrospector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@SpringBootTest
@RunWith(JUnit4.class)
public class WebClientTest {


    private URI introspectionUri = URI.create("http://localhost:7080/oauth/check_token");

    private WebClient webClient;

    private String token = "3c0ab41e-42b0-4e71-a9c9-92be535c2a2d";

    @Before
    public void before() {
        this.webClient = WebClient.builder()
                .defaultHeaders(h -> h.setBasicAuth("0001", "123456"))
                .build();

        System.out.println("-----------------------");
    }

    @Test
    public void webClientTest() {


        Mono<ClientResponse> responseMono = this.webClient.post()
                .uri(this.introspectionUri)
//                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(BodyInserters.fromFormData("token", token))
                .exchange();

        ClientResponse response = responseMono.block();

        HttpStatus httpStatus = HttpStatus.valueOf(response.rawStatusCode());
        System.out.println(httpStatus);
        System.out.println(response.headers());

        if (httpStatus == HttpStatus.OK) {
            String content = response.bodyToMono(String.class).block();
            System.out.println(content);
        }


    }

    @Test
    public void reactiveClientTest() {
        System.out.println("--- reactiveClientTest");
        RoTokenReactiveIntrospector roTokenReactiveIntrospector = new RoTokenReactiveIntrospector("http://localhost:7080/oauth/check_token", "0001", "123456");
        OAuth2AuthenticatedPrincipal principal = roTokenReactiveIntrospector.introspect(token).block();


    }


    @After
    public void after() {
        System.out.println("-----------------------");
    }
}
