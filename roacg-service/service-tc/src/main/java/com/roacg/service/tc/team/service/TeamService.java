package com.roacg.service.tc.team.service;

import com.roacg.service.tc.team.model.dto.TeamDTO;

import java.util.Optional;

/**
 * 组服务
 */
public interface TeamService {

    Optional<TeamDTO> findTeamInfo(Long teamId);
}
