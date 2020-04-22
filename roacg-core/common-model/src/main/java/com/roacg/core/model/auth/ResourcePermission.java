package com.roacg.core.model.auth;

import com.roacg.core.model.auth.enmus.PermissionType;
import lombok.Data;

import java.util.Arrays;
import java.util.StringJoiner;

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

    @Override
    public String toString() {
        return new StringJoiner(",", "{", "}")
                .add("url: '" + url + "'")
                .add("method: '" + method + "'")
                .add("type: '" + type + "'")
                .add("roles: " + Arrays.toString(roles))
                .toString();
    }
}
