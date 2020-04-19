package com.roacg.core.model.auth.enmus;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.auth.ResourcePermission;

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

            return false;
        }
    },
    HAS_ANY_ROLE() {
        @Override
        public boolean hasResourcePermission(RequestUser user, ResourcePermission permission) {
            return false;
        }
    };


    public abstract boolean hasResourcePermission(RequestUser user, ResourcePermission permission);

}
