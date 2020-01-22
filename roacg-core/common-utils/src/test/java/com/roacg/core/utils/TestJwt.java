package com.roacg.core.utils;

import com.roacg.core.utils.sejwt.jwt.JwtBuilder;
import com.roacg.core.utils.sejwt.jwt.Payload;
import com.roacg.core.utils.sejwt.jwt.SeJwt;
import com.roacg.core.utils.sejwt.jwt.en.JwtAttribute;
import com.roacg.core.utils.sejwt.jwt.token.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@RunWith(JUnit4.class)
@SpringBootTest
public class TestJwt {


    @Test
    public void testJwt() throws InterruptedException {
        Payload payload = new Payload.PayloadBuilder()
                .nbf(LocalDateTime.now())
                .exp(3 * 1000L)
                .data("test", "test value")
                .build();
        String jwt = new JwtBuilder().payload(payload).build("This is key secret");

        System.out.println(jwt);


        Token token = SeJwt.asToken(jwt).signatureSecret("Wrong secret");
        System.out.println("------ Token with wrong secret");
        System.out.println(token.getHeaderJson());
        System.out.println(token.getPayloadJson());
        System.out.println(token.tokenIsCorrect());
        System.out.println(token.tokenHasExpired());
        System.out.println(token.validateToken());
        System.out.println(token.getAttributeValue(JwtAttribute.Payload.NBF).get());
        System.out.println(token.getAttributeValue("test").get()); //getAttributeValue() return Optional

        token = token.signatureSecret("This is key secret");
        System.out.println("------ Correct token");
        System.out.println(token.tokenIsCorrect());
        System.out.println(token.tokenHasExpired());
        System.out.println(token.validateToken());

        Thread.sleep(3 * 1000L);
        System.out.println("------ After the token expires");
        System.out.println(token.tokenIsCorrect());
        System.out.println(token.tokenHasExpired());
        System.out.println(token.validateToken());


        Token wrongToken = SeJwt.asToken("12312325afvbascAJ--ASD=");
        System.out.println("------ Wrong token");
        System.out.println(wrongToken.getHeaderJson());
        System.out.println(wrongToken.getPayloadJson());
        System.out.println(wrongToken.tokenIsCorrect());
        System.out.println(wrongToken.tokenHasExpired());
        System.out.println(wrongToken.validateToken());


    }




}