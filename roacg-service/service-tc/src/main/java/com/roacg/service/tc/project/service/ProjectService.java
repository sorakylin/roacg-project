package com.roacg.service.tc.project.service;

import com.roacg.service.tc.project.model.dto.SimpleProjectDTO;
import com.roacg.service.tc.project.model.req.ProjectCreateREQ;

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


    /**
     * 创建一个项目
     *
     * @param req 请求体
     */
    void createProject(ProjectCreateREQ req);


    //给项目拉人,传个项目ID 和一堆用户ID
    void bindUsers(Long projectId, Long... userIds);
}
