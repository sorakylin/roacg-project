package com.roacg.core.model.auth;

import lombok.Data;

@Data
public class ResourcePermission {

    private String url;

    private String method;

    private String type;

    private String[] roles;
}
