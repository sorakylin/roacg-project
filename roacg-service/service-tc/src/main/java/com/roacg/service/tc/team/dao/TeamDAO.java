package com.roacg.service.tc.team.dao;

import com.roacg.service.tc.team.model.po.TeamPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamDAO extends JpaRepository<TeamPO, Long> {
    
}
