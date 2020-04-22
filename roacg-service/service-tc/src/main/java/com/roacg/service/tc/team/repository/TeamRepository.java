package com.roacg.service.tc.team.repository;

import com.roacg.service.tc.team.model.po.TeamPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<TeamPO, Long> {

    int countByTeamName(String teamName);

}
