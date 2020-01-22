package com.roacg.core.model.db;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity implements DBMapEntity {

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

}
