package com.roacg.service.tc.team.repository;

import com.roacg.service.tc.team.model.po.TeamUserPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TeamUserRepository extends CrudRepository<TeamUserPO, Long> {

    int countByUserId(Long userId);

    Page<TeamUserPO> findByUserId(Long userId, Pageable page);

}
