package com.roacg.core.model.auth.enmus;

public enum PermissionType {

    PUBLIC,
    LOGIN,
    HAS_ROLE;


    public String getKey(String keyPrefix) {
        return new StringBuilder(keyPrefix).append(':').append(this.name()).toString();
    }
}
