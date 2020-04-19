package com.roacg.service.gateway.security;

import com.roacg.core.base.enmus.ServiceCode;
import com.roacg.core.model.auth.ResourcePermission;
import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.consts.RoAuthConst;
import com.roacg.core.utils.context.RoContext;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class PermissionVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionVerifier.class);


    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final Map<ServiceCode, List<ResourcePermission>> PERMISSION_MAP = new HashMap<>();

    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;


    public PermissionVerifier() {

        List<ResourcePermission> resourcePermissions = List.of(
                ResourcePermission.create("/login", HttpMethod.POST.name(), PermissionType.PUBLIC),
                ResourcePermission.create("/register", HttpMethod.POST.name(), PermissionType.PUBLIC)
        );

        PERMISSION_MAP.put(ServiceCode.GATEWAY, resourcePermissions);
    }

    /**
     * 校验是否属于可放行的资源
     *
     * @param requestPath 请求路径
     * @return true 可放行， false反之
     */
    public static boolean checkResource(String requestPath, HttpMethod method) {
        if (!StringUtils.hasText(requestPath)) return false;

        //试图请求的服务
        ServiceCode requestService = Arrays.stream(ServiceCode.values()).parallel()
                .filter(en -> requestPath.startsWith(String.format("/%s/", en.getCode())))
                .findFirst()
                .orElse(ServiceCode.GATEWAY);


        //该服务下的所有资源许可
        List<ResourcePermission> resourcePermissions = PERMISSION_MAP.get(requestService);

        if (CollectionUtils.isEmpty(resourcePermissions)) {
            return false;
        }

        //真实请求的服务路径
        final String servicePath = ServiceCode.GATEWAY == requestService ?
                requestPath : requestPath.substring(requestService.getCode().length() + 1);


        return resourcePermissions
                .parallelStream()
                .filter(r -> ANT_PATH_MATCHER.match(r.getUrl(), servicePath))//留下路径匹配的
                .filter(r -> Objects.nonNull(r.getMethod()))
                .filter(r -> "*".equals(r.getMethod()) ||  //留下方法匹配的
                        ArrayUtils.contains(r.getMethod().toUpperCase().split(","), method.name()))
                .anyMatch(PermissionVerifier::checkResource);
    }

    public static boolean checkResource(ResourcePermission resource) {
        PermissionType type = PermissionType.valueOf(resource.getType());
        if (Objects.isNull(type)) return false;
        return type.hasResourcePermission(RoContext.getRequestUser(), resource);
    }


    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void refreshPermissionMap() {
        LOGGER.info("Refresh permission map...");

        for (ServiceCode code : ServiceCode.values()) {
            //这里先跳过自己
            if (code == ServiceCode.GATEWAY) continue;

            String resourceKey = RoAuthConst.getExposeResourceKey(code.getApplicationName());

            ReactiveListOperations<String, ResourcePermission> listOperations = reactiveRedisTemplate.opsForList();


            listOperations.range(resourceKey, 0, -1)
                    .collect(Collectors.toUnmodifiableList())
                    .subscribe(list -> PERMISSION_MAP.put(code, list));
        }


    }


}
