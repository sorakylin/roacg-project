package com.roacg.service.system.rouser.model.dto;

import lombok.Data;

import java.io.Serializable;

//最低限度的资源权限信息
@Data
public class MinimumResourcePermissionDTO implements Serializable {

    private Long permissionId;
    private String name;
    private String enName;
    private String url;
    private String method;

}
