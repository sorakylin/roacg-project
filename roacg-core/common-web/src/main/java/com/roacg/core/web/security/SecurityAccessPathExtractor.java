package com.roacg.core.web.security;

import com.roacg.core.model.auth.ResourcePermission;
import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.consts.RoAuthConst;
import com.roacg.core.web.security.annotation.ExposeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;


public class SecurityAccessPathExtractor implements CommandLineRunner {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void run(String... args) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();

        List<ResourcePermission> resourcePermissions = handlerMethods.entrySet().stream()
                .map(e -> pathExtractor(e.getKey(), e.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());

        if (resourcePermissions.isEmpty()) return;

        String resourceKey = RoAuthConst.getExposeResourceKey(applicationName);
        redisTemplate.delete(resourceKey);
        redisTemplate.opsForList().leftPushAll(resourceKey, resourcePermissions);
    }


    public Optional<ResourcePermission> pathExtractor(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {

        ExposeResource exposeResource = handlerMethod.getBeanType().getAnnotation(ExposeResource.class);
        ExposeResource methodResource = handlerMethod.getMethodAnnotation(ExposeResource.class);

        if (Objects.isNull(exposeResource) && Objects.isNull(methodResource)) {
            return Optional.empty();
        }

        //方法上的注解覆盖类上的注解
        if (Objects.nonNull(methodResource)) {
            exposeResource = methodResource;
        }

        //url和请求方法
        PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
        Set<String> patterns = patternsCondition.getPatterns();
        Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();

        //权限实体
        ResourcePermission value = new ResourcePermission();

        if (methods.isEmpty()) {
            value.setMethod("*");
        } else {
            String methodValue = methods.stream()
                    .map(Objects::toString)
                    .collect(joining(","));
            value.setMethod(methodValue);
        }

        patterns.stream().findFirst().ifPresent(value::setUrl);

        PermissionType type = exposeResource.type();
        value.setType(type.name());

        value.setRoles(exposeResource.roles());

        return Optional.of(value);
    }


}
