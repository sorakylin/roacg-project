package com.roacg.service.tc.team.repository;

import com.roacg.service.tc.team.model.po.TeamPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<TeamPO, Long> {

    int countByTeamName(String teamName);


    @Modifying
    @Query("UPDATE TeamPO team SET team.projectNum=team.projectNum+1 WHERE team.teamId=:teamId")
    int incrementProjectNum(@Param("teamId") Long teamId);

    @Query("SELECT teamName FROM TeamPO WHERE teamId=:teamId")
    String findNameByTeamId(Long teamId);
}
