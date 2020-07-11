package com.roacg.core.model.db.auditing;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.context.RoContext;
import com.roacg.core.model.db.BaseEntity;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class RoAuditingListener {

    @PrePersist
    public void prePersist(BaseEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Entity must not be null!");
        }

        RequestUser currentUser = RoContext.getRequestUser();
        if (currentUser.hasLogin()) {
            LocalDateTime now = LocalDateTime.now();

            entity.setCreateAt(currentUser.getName());
            entity.setCreateTime(now);
            entity.setUpdateAt(currentUser.getName());
            entity.setUpdateTime(now);
            entity.setUpdateId(currentUser.getId());
        }
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Entity must not be null!");
        }

        RequestUser currentUser = RoContext.getRequestUser();
        if (currentUser.hasLogin()) {
            entity.setUpdateAt(currentUser.getName());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setUpdateId(currentUser.getId());
        }
    }

    @PostPersist
    public void postPersist(BaseEntity entity) {
    }

    @PostUpdate
    public void postUpdate(BaseEntity entity) {
    }
}
