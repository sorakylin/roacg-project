package com.roacg.service.tc.project.repository;

import com.roacg.core.model.enums.DeletedStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import com.roacg.service.tc.project.model.po.ProjectPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectPO, Long> {

    List<ProjectPO> findAllByTeamId(Long teamId);

    int countByProjectNameAndTeamIdAndDeleted(String projectName, Long teamId, DeletedStatusEnum deleted);

    int countByProjectNameAndProjectType(String projectName, ProjectTypeEnum projectTypeEnum);
}
