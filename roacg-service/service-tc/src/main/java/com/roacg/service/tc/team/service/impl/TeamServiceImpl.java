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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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
    public List<TeamDTO> findMyTeams() {
        RequestUser currentUser = RoContext.getRequestUser();

        Sort sort = Sort.by("userTeamRole");

        //当前用户拥有的团队, 只查询前六个
        Page<TeamUserPO> userTeam = teamUserRepository.findByUserId(currentUser.getId(), PageRequest.of(1, 6, sort));

        if (userTeam.isEmpty()) return List.of();

        //查询出详细信息
        List<TeamPO> teams = userTeam.stream().map(TeamUserPO::getTeamId).distinct()
                .collect(collectingAndThen(toList(), teamRepository::findAllById));

        return teams.stream().map(TeamDTO::from).collect(toList());
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

        int userTeamNum = teamUserRepository.countByUserId(currentUser.getId());
        if (userTeamNum > 6) {
            throw new ParameterValidationException("加入的团队已达到上限!");
        }

        //先保存小组
        TeamPO entity = req.transferToDTO().transferToEntity();
        teamRepository.saveAndFlush(entity);

        if (Objects.isNull(entity.getTeamId())) {
            throw new RoApiException(RoApiStatusEnum.OTHER, "保存失败,请重试!");
        }

        //再保存关联关系
        TeamUserPO teamUser = new TeamUserPO();
        teamUser.setTeamId(entity.getTeamId());
        teamUser.setUserId(currentUser.getId());
        teamUser.setUserTeamRole(UserTeamRoleEnum.TEAM_LEADER);
        teamUserRepository.save(teamUser);

    }
}
