package com.roacg.core.model.db.support;


import com.roacg.core.model.db.BaseEntity;
import com.roacg.core.model.enums.DeletedStatusEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * 软删除支持
 */
@Data
@MappedSuperclass
public class VirtualDeleteSupportEntity extends BaseEntity {


    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "DELETED", length = 2, nullable = false)
    private DeletedStatusEnum deleted;


    public boolean hasDeleted() {
        return DeletedStatusEnum.DELETED.equals(deleted);
    }


}
