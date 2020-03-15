package com.roacg.core.model.db.support;


import com.roacg.core.model.db.BaseEntity;
import com.roacg.core.model.enums.DeletedStatusEnum;
import com.roacg.core.model.enums.convert.BaseCodeEnumConvertFactory;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

/**
 * 软删除支持
 */
@Data
@MappedSuperclass
public class VirtualDeleteSupportEntity extends BaseEntity {


//    @Enumerated(value = EnumType.ORDINAL)
    @Convert(converter = BaseCodeEnumConvertFactory.DeletedStatusEnumConvert.class)
    @Column(name = "DELETED", length = 2, nullable = false, columnDefinition = "tinyint(1)")
    private DeletedStatusEnum deleted;


    public boolean hasDeleted() {
        return DeletedStatusEnum.DELETED.equals(deleted);
    }


}
