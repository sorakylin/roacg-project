package com.roacg.core.model.db;

import com.roacg.core.model.enums.DeletedStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BasicCompleteEntity implements DBMapEntity {

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @Column(name = "CREATE_AT")
    private String createAt;

    @Column(name = "UPDATE_AT")
    private String updateAt;

    @Column(name = "UPDATE_ID")
    private Long updateId;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "DELETED", length = 2, nullable = false)
    private DeletedStatusEnum deleted;

    @Version
    @Column(name = "VERSION")
    private int version;
}
