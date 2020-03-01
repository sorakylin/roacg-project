package com.roacg.service.system.rouser.model.dto;

import lombok.Data;

//最低限度的角色信息
@Data
public class MinimumRoleDTO {

    private Long roleId;

    private Long parentId;

    private String name;

    private String enName;

}
