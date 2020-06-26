package com.roacg.service.tc.project.service;

import com.roacg.service.tc.project.model.dto.SimpleProjectDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectService {


    Optional<SimpleProjectDTO> findSimpleProject(Long projectId);


    List<SimpleProjectDTO> findSimpleProject(Collection<Long> projectId);

    /**
     * 查询和指定小组相关联的项目
     *
     * @param teamId 小组ID/团队ID
     * @return 项目
     */
    List<SimpleProjectDTO> findTeamSimpleProject(Long teamId);

    /**
     * 查询和项目有关联的用户ID
     *
     * @param projectId 项目ID
     * @return 和项目有关联的用户ID
     */
    List<Long> findProjectUserIds(Long projectId);
}
