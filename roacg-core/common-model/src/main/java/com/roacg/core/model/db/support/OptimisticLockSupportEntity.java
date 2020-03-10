package com.roacg.core.model.db.support;

import com.roacg.core.model.db.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * 乐观锁支持
 */
@Data
@MappedSuperclass
public class OptimisticLockSupportEntity extends BaseEntity {

    @Version
    @Column(name = "VERSION", columnDefinition = "tinyint(3)")
    private int version;
}
