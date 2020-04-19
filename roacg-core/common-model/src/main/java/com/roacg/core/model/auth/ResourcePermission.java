package com.roacg.core.model.auth;

import com.roacg.core.model.auth.enmus.PermissionType;
import lombok.Data;

@Data
public class ResourcePermission {

    private String url;

    private String method;

    private String type;

    private String[] roles;

    public static ResourcePermission create(String url, String method, PermissionType type) {
        ResourcePermission r = new ResourcePermission();
        r.setUrl(url);
        r.setMethod(method);
        r.setType(type.name());
        return r;
    }

    public static ResourcePermission create(String url, String method, PermissionType type, String... roles) {
        ResourcePermission permission = ResourcePermission.create(url, method, type);
        permission.setRoles(roles);
        return permission;
    }
}
