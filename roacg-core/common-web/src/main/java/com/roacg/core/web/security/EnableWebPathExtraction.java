package com.roacg.core.web.security;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ImportAutoConfiguration(SecurityAccessPathExtractor.class)
public @interface EnableWebPathExtraction {

}
