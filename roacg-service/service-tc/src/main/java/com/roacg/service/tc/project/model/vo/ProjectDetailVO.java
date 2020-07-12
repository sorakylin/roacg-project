package com.roacg.service.tc.project.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.roacg.service.tc.project.enums.ProjectStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import lombok.Data;

@Data
public class ProjectDetailVO {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long projectId;

    private String projectName;

    //项目简介
    private String projectProfile;

    //从哪里来
    private String fromLanguage;

    //到哪里去
    private String toLanguage;

    //项目的状态
    private ProjectStatusEnum projectStatus;

    //项目类型
    private ProjectTypeEnum projectType;

    //团队ID
    private Long teamId;

    //项目建立人 userId
    private Long founderId;

    private String createUserName;

    private String createTeamName;
}
