package com.roacg.core.model.db;

import com.roacg.core.model.enums.DeletedStatusEnum;
import com.roacg.core.model.enums.convert.BaseCodeEnumConvertFactory;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@Data
@MappedSuperclass
public class BasicCompleteEntity extends BaseEntity {

    private static final long serialVersionUID = -1;


//    @Enumerated(value = EnumType.ORDINAL)
    @Convert(converter = BaseCodeEnumConvertFactory.DeletedStatusEnumConvert.class)
    @Column(name = "DELETED", length = 2, nullable = false, columnDefinition = "tinyint(1)")
    private DeletedStatusEnum deleted;

    @Version
    @Column(name = "VERSION", columnDefinition = "tinyint(1)")
    private int version;


    public boolean hasDeleted() {
        return DeletedStatusEnum.DELETED.equals(deleted);
    }
}
