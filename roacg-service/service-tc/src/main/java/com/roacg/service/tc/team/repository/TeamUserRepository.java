package com.roacg.service.tc.team.repository;

import com.roacg.service.tc.team.model.po.TeamUserPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamUserRepository extends CrudRepository<TeamUserPO, Long> {

    int countByUserId(Long userId);

    Page<TeamUserPO> findByUserId(Long userId, Pageable page);

    @Query("SELECT tu.userId FROM TeamUserPO tu WHERE tu.teamId=:teamId")
    List<Long> findUserIdsByTeamId(@Param("teamId") Long teamId);
}
