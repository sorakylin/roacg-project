package com.roacg.service.system.rouser.model.po;

import com.roacg.core.model.db.BaseEntity;
import lombok.Data;

import javax.persistence.Table;

//tb_permission 权限表
@Table(name = "tb_resource_permission")
@Data
public class ResourcePermissionPO extends BaseEntity {

    private Long permissionId;
    private String url;
    private String method;
    private String name;
    private String enName;
    private String description;

}
