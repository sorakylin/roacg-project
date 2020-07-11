package com.roacg.core.model.db;

import com.roacg.core.model.db.auditing.RoAuditingListener;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(value = RoAuditingListener.class)
public class BaseEntity implements DBMapEntity {

    private static final long serialVersionUID = -1;

    @Column(name = "CREATE_TIME", columnDefinition = "datetime")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME", columnDefinition = "datetime")
    private LocalDateTime updateTime;

    @Column(name = "CREATE_AT", columnDefinition = "varchar(64)")
    private String createAt;

    @Column(name = "UPDATE_AT", columnDefinition = "varchar(64)")
    private String updateAt;

    @Column(name = "UPDATE_ID", columnDefinition = "bigint(20)")
    private Long updateId;

}
