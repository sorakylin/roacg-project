package com.roacg.service.tc.project.repository;

import com.roacg.service.tc.project.model.po.ProjectUserPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectUserRepository extends CrudRepository<ProjectUserPO, Long> {

    @Query("SELECT p.userId FROM ProjectUserPO p WHERE p.projectId=:projectId")
    List<Long> findUserIdByProjectId(@Param("projectId") Long projectId);
}
