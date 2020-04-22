package com.roacg.service.tc.team.service.impl;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.utils.context.RoContext;
import com.roacg.service.tc.team.enums.UserTeamRoleEnum;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.po.TeamPO;
import com.roacg.service.tc.team.model.po.TeamUserPO;
import com.roacg.service.tc.team.model.req.TeamCreateREQ;
import com.roacg.service.tc.team.repository.TeamRepository;
import com.roacg.service.tc.team.repository.TeamUserRepository;
import com.roacg.service.tc.team.service.TeamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {

    //Spring beans -----------------
    private TeamRepository teamRepository;
    private TeamUserRepository teamUserRepository;


    @Override
    public Optional<TeamDTO> findTeamInfo(Long teamId) {
        if (Objects.isNull(teamId)) {
            throw new ParameterValidationException("团队ID不能为空!");
        }

        //实体
        Optional<TeamPO> teamPO = teamRepository.findById(teamId);

        return teamPO.map(TeamDTO::from);
    }

    @Override
    public List<TeamDTO> findTeamPageByCreateTime(int pageNum, int pageSize) {
        //根据创建时间倒序, 最新的在最上面
        Sort descCreateSort = Sort.by(Sort.Order.desc("createTime"));

        PageRequest pageReq = PageRequest.of(pageNum, pageSize, descCreateSort);

        Page<TeamPO> result = teamRepository.findAll(pageReq);

        return result.get().map(TeamDTO::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createTeam(TeamCreateREQ req) {

        RequestUser currentUser = RoContext.getRequestUser();
        log.info("用户:{} (ID:{}) 试图创建团队:{}", currentUser.getName(), currentUser.getId(), req.getTeamName());

        int count = teamRepository.countByTeamName(req.getTeamName());
        if (count > 0) {
            throw new ParameterValidationException("团队名已存在!");
        }

        //先保存小组
        TeamPO entity = req.transferToDTO().transferToEntity();
        teamRepository.saveAndFlush(entity);

        if (Objects.isNull(entity.getTeamId())) {
            throw new RoApiException(RoApiStatusEnum.OTHER, "保存失败,请重试!");
        }

        //在保存关联关系
        TeamUserPO teamUser = new TeamUserPO();
        teamUser.setTeamId(entity.getTeamId());
        teamUser.setUserId(currentUser.getId());
        teamUser.setUserTeamRole(UserTeamRoleEnum.TEAM_LEADER);
        teamUserRepository.save(teamUser);

    }
}
