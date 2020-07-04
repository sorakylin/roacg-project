package com.roacg.service.tc.project.model.req;

import com.roacg.service.tc.project.enums.ProjectPermissionStatusEnum;
import com.roacg.service.tc.project.enums.ProjectStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ProjectCreateREQ {

    @NotEmpty
    private String projectName;

    //项目简介
    private String projectProfile;

    //项目类型
    @NotNull
    private ProjectTypeEnum projectType;

    //项目的许可范围, 标识了该项目可以被谁访问
    @NotNull
    private ProjectPermissionStatusEnum projectPermissionStatus;

    //团队ID
    private Long teamId;

    @Setter(value = AccessLevel.NONE)
    private ProjectStatusEnum projectStatus = ProjectStatusEnum.NOT_STARTED;
}
