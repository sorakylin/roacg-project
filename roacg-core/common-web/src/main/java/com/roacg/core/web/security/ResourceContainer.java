package com.roacg.core.web.security;

import com.roacg.core.model.auth.ResourcePermission;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ResourceContainer {

    private static List<ResourcePermission> resourcePermissions;

    protected static void setResourcePermissions(List<ResourcePermission> permissions) {
        ResourceContainer.resourcePermissions = permissionsCopy(permissions);
    }

    protected static void addResourcePermissions(List<ResourcePermission> permissions) {
        permissions = permissionsCopy(permissions);

        if (ResourceContainer.resourcePermissions == null) {
            ResourceContainer.resourcePermissions = permissions;
        } else {
            ResourceContainer.resourcePermissions.addAll(permissions);
        }

    }

    public static List<ResourcePermission> getResourcePermissions() {
        return permissionsCopy(ResourceContainer.resourcePermissions);
    }


    private static List<ResourcePermission> permissionsCopy(List<ResourcePermission> source) {
        if (Objects.isNull(source)) return new LinkedList<>();

        return source.stream().map(ResourcePermission::copy).collect(Collectors.toList());
    }
}
