package com.roacg.service.tc.team.service.impl;

import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.po.TeamPO;
import com.roacg.service.tc.team.repository.TeamRepository;
import com.roacg.service.tc.team.service.TeamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {

    //Spring beans -----------------
    private TeamRepository teamDAO;


    @Override
    public Optional<TeamDTO> findTeamInfo(Long teamId) {
        if (Objects.isNull(teamId)) {
            throw new ParameterValidationException("团队ID不能为空!");
        }

        //实体
        Optional<TeamPO> teamPO = teamDAO.findById(teamId);

        return teamPO.map(TeamDTO::from);
    }

    @Override
    public List<TeamDTO> findTeamPageByCreateTime(int pageNum, int pageSize) {
        //根据创建时间倒序, 最新的在最上面
        Sort descCreateSort = Sort.by(Sort.Order.desc("createTime"));

        PageRequest pageReq = PageRequest.of(pageNum, pageSize, descCreateSort);

        Page<TeamPO> result = teamDAO.findAll(pageReq);

        return result.get().map(TeamDTO::from).collect(Collectors.toList());
    }
}
