package com.roacg.core.web.security.annotation;


import com.roacg.core.model.auth.enmus.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在方法/类上使用此注解， 表示向外部暴露一个资源
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ExposeResource {

    PermissionType type() default PermissionType.PUBLIC;

    String[] roles() default {};
}
