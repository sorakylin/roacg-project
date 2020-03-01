package com.roacg.service.system.rouser.model.po;

import com.roacg.core.model.db.BaseEntity;
import lombok.Data;

import javax.persistence.Table;

@Table(name = "tb_role")
@Data
public class RolePO extends BaseEntity {

    private Long roleId;

    private Long parentId;

    private String name;

    private String enName;

    private String description;

}
