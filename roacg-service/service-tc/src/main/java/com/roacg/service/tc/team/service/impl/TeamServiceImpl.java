package com.roacg.service.tc.team.service.impl;

import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.service.tc.team.repository.TeamRepository;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.po.TeamPO;
import com.roacg.service.tc.team.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    private Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    //Spring beans -----------------

    private TeamRepository teamDAO;

    public TeamServiceImpl(TeamRepository teamDAO) {
        this.teamDAO = teamDAO;
    }


    @Override
    public Optional<TeamDTO> findTeamInfo(Long teamId) {
        if (Objects.isNull(teamId)) {
            throw new ParameterValidationException("团队ID不能为空!");
        }

        //实体
        Optional<TeamPO> teamPO = teamDAO.findById(teamId);

        return teamPO.map(TeamDTO::from);
    }
}
