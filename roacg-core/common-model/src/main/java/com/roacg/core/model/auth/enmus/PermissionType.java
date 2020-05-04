package com.roacg.core.model.auth.enmus;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.auth.ResourcePermission;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum PermissionType {

    PUBLIC() {
        @Override
        public boolean hasResourcePermission(RequestUser user, ResourcePermission permission) {
            return true;
        }
    },
    LOGIN() {
        @Override
        public boolean hasResourcePermission(RequestUser user, ResourcePermission permission) {
            return user.hasLogin();
        }
    },
    HAS_ROLE() {
        @Override
        public boolean hasResourcePermission(RequestUser user, ResourcePermission permission) {
            if (Objects.isNull(permission.getRoles()) || permission.getRoles().length == 0) return true;

            List<String> authorities = user.getAuthorities();
            if (authorities == null || authorities.isEmpty()) return false;

            return Arrays.stream(permission.getRoles()).allMatch(authorities::contains);
        }
    },
    HAS_ANY_ROLE() {
        @Override
        public boolean hasResourcePermission(RequestUser user, ResourcePermission permission) {
            if (Objects.isNull(permission.getRoles()) || permission.getRoles().length == 0) return true;

            List<String> authorities = user.getAuthorities();
            if (authorities == null || authorities.isEmpty()) return false;

            return Arrays.stream(permission.getRoles()).anyMatch(authorities::contains);
        }
    };


    public abstract boolean hasResourcePermission(RequestUser user, ResourcePermission permission);

}
