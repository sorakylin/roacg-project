package com.roacg.core.web.security;

import com.roacg.core.model.auth.ResourcePermission;
import com.roacg.core.model.consts.RoAuthConst;
import com.roacg.core.web.security.annotation.ExposeResource;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 抽取 SpringMVC 中所有的暴露出的资源
 * 封装为具体的资源许可
 */
@Slf4j
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

        log.info("[{}] SpringMVC 路径抽取完毕, 暴露接口数: {}", applicationName, resourcePermissions.size());
        resourcePermissions.forEach(p -> log.debug(p.toString()));

        //设置进资源容器中
        ResourceContainer.setResourcePermissions(resourcePermissions);
    }


    public Optional<ResourcePermission> pathExtractor(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {

        ExposeResource exposeResource = handlerMethod.getBeanType().getAnnotation(ExposeResource.class);
        ExposeResource methodResource = handlerMethod.getMethodAnnotation(ExposeResource.class);

        if (Objects.isNull(exposeResource) && Objects.isNull(methodResource)) {
            return Optional.empty();
        }

        //方法上的注解比类上的注解优先级更高
        if (Objects.nonNull(methodResource)) {
            exposeResource = methodResource;
        }

        //url和请求方法
        PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
        Set<String> patterns = patternsCondition.getPatterns();
        Set<RequestMethod> methods = requestMappingInfo.getMethodsCondition().getMethods();

        //权限实体
        ResourcePermission permission = new ResourcePermission();
        permission.setType(exposeResource.type().name());
        permission.setRoles(exposeResource.roles());

        //url设置, 只拿第一个。
        patterns.stream().findFirst().map(url -> url.replaceAll("\\{.+\\}", "*")).ifPresent(permission::setUrl);

        //http method 设置
        if (methods.isEmpty()) {
            permission.setMethod("*");
        } else {
            String methodValue = methods.stream()
                    .map(Objects::toString)
                    .collect(joining(","));
            permission.setMethod(methodValue);
        }

        return Optional.of(permission);
    }


}
