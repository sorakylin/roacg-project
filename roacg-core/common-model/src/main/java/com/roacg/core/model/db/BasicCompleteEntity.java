package com.roacg.core.model.db;

import com.roacg.core.model.enums.DeletedStatusEnum;
import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
public class BasicCompleteEntity extends BaseEntity {

    private static final long serialVersionUID = -1;


    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "DELETED", length = 2, nullable = false, columnDefinition = "tinyint(3)")
    private DeletedStatusEnum deleted;

    @Version
    @Column(name = "VERSION", columnDefinition = "tinyint(3)")
    private int version;


    public boolean hasDeleted() {
        return DeletedStatusEnum.DELETED.equals(deleted);
    }
}
