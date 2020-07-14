package com.roacg.service.tc.project.model.dto;

import lombok.Data;

@Data
public class ProjectNameDTO {

    private Long projectId;

    private String projectName;

    public static ProjectNameDTO from(SimpleProjectDTO simpleProjectDTO){
        ProjectNameDTO projectNameDTO = new ProjectNameDTO();
        projectNameDTO.setProjectId(simpleProjectDTO.getProjectId());
        projectNameDTO.setProjectName(simpleProjectDTO.getProjectName());
        return projectNameDTO;
    }
}
