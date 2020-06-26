package com.roacg.service.tc.project.model.dto;


import com.roacg.service.tc.project.enums.ProjectPermissionStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import lombok.Data;

@Data
public class SimpleProjectDTO {

    private Long projectId;

    private String projectName;

    //项目简介
    private String projectProfile;
    
    //项目类型
    private ProjectTypeEnum projectType;

    //项目的许可范围, 标识了该项目可以被谁访问
    private ProjectPermissionStatusEnum projectPermissionStatus;
}
