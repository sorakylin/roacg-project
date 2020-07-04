package com.roacg.service.tc.project.model.dto;


import com.roacg.service.tc.project.enums.ProjectPermissionStatusEnum;
import com.roacg.service.tc.project.enums.ProjectStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import lombok.Data;

@Data
public class SimpleProjectDTO {

    private Long projectId;

    private String projectName;

    //项目简介
    private String projectProfile;

    //源语言
    private String fromLanguage;

    //新语言
    private String toLanguage;

    //项目状态
    private ProjectStatusEnum projectStatus;

    //项目类型
    private ProjectTypeEnum projectType;

    //项目的许可范围, 标识了该项目可以被谁访问
    private ProjectPermissionStatusEnum projectPermissionStatus;
}
