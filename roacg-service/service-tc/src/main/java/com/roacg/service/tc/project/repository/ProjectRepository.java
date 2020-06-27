package com.roacg.service.tc.project.repository;

import com.roacg.service.tc.project.model.po.ProjectPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectPO, Long> {

    List<ProjectPO> findAllByTeamId(Long teamId);
}
