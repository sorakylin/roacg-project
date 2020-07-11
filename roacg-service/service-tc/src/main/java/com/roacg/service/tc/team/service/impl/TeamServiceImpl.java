package com.roacg.service.tc.team.service.impl;

import com.roacg.api.system.service.UserRService;
import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.core.model.context.RoContext;
import com.roacg.core.web.model.PageResponse;
import com.roacg.service.tc.project.service.ProjectService;
import com.roacg.service.tc.team.enums.UserTeamRoleEnum;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.po.TeamPO;
import com.roacg.service.tc.team.model.po.TeamUserPO;
import com.roacg.service.tc.team.model.req.TeamCreateREQ;
import com.roacg.service.tc.team.model.req.TeamUpdateREQ;
import com.roacg.service.tc.team.model.vo.MyTeamsVO;
import com.roacg.service.tc.team.model.vo.TeamDetailVO;
import com.roacg.service.tc.team.model.vo.TeamSimpleUserVO;
import com.roacg.service.tc.team.repository.TeamRepository;
import com.roacg.service.tc.team.repository.TeamUserRepository;
import com.roacg.service.tc.team.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamUserRepository teamUserRepository;
    @Autowired
    private ProjectService projectService;

    @Reference
    private UserRService userRService;

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
    public PageResponse<MyTeamsVO> findMyTeams() {
        RequestUser currentUser = RoContext.getRequestUser();

        Sort sort = Sort.by("userTeamRole");

        //当前用户加入的团队, 只查询前六个
        Page<TeamUserPO> userTeamPage = teamUserRepository.findAllByUserId(currentUser.getId(), PageRequest.of(0, 6, sort));

        if (userTeamPage.isEmpty()) return PageResponse.empty();

        //key teamId, value my role
        Map<Long, UserTeamRoleEnum> teamRoleMapping = userTeamPage.stream()
                .collect(toMap(TeamUserPO::getTeamId, TeamUserPO::getUserTeamRole));

        //查询出详细信息
        List<TeamPO> teams = teamRepository.findAllById(teamRoleMapping.keySet());

        List<MyTeamsVO> myTeams = teams.stream().map(team -> {
            MyTeamsVO vo = new MyTeamsVO();
            vo.setTeamId(team.getTeamId());
            vo.setTeamName(team.getTeamName());
            vo.setAvatar(team.getAvatar());
            vo.setTeamProfile(team.getTeamProfile());
            vo.setTeamSize(team.getTeamSize());
            vo.setProjectNum(team.getProjectNum());

            //设置角色
            vo.setMyRole(teamRoleMapping.get(team.getTeamId()).getCode());

            return vo;
        }).collect(toList());

        return PageResponse.of(myTeams).page(userTeamPage);
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
        if (userTeamNum >= 6) {
            throw new ParameterValidationException("加入的团队已达到上限!");
        }

        TeamDTO saveDto = req.transferToDTO();
        saveDto.setTeamSize(1);
        saveDto.setProjectNum(0);
        saveDto.setTeamGrade(1);//团队等级,根据等级 相应的权限也有所不同 TODO 没这功能，先写个1
        saveDto.setLeaderId(currentUser.getId());

        //先保存小组
        TeamPO entity = saveDto.transferToEntity();
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

    @Override
    @Transactional
    public void updateTeam(TeamUpdateREQ req) {
        RequestUser currentUser = RoContext.getRequestUser();

        TeamPO existedTeam = teamRepository.findById(req.getTeamId()).orElseThrow(() -> new ParameterValidationException("无对应小组!"));

        if (!Objects.equals(existedTeam.getLeaderId(), currentUser.getId())) {
            throw new ParameterValidationException("无法修改");
        }

        existedTeam.setTeamName(req.getTeamName());
        existedTeam.setAvatar(req.getAvatar());
        existedTeam.setTeamProfile(req.getTeamProfile());
        existedTeam.setTeamDescription(req.getTeamDescription());

        try {
            teamRepository.saveAndFlush(existedTeam);
        } catch (DuplicateKeyException e) {
            throw new ParameterValidationException("团队名重复!");
        }
    }

    @Override
    public TeamDetailVO findTeamDetail(Long teamId) {
        TeamDetailVO detail = new TeamDetailVO();
        TeamDTO team = this.findTeamInfo(teamId).orElseThrow(ParameterValidationException::new);
        BeanMapper.map(team, detail);

        //当前团队的用户  key: userId ; value: userTeamRole
        Map<Long, Integer> teamUserRoleMapping = teamUserRepository.findAllByTeamId(teamId)
                .stream()
                .collect(toMap(t -> t.getUserId(), t -> t.getUserTeamRole().getCode()));

        List<TeamSimpleUserVO> users = userRService.findUserName(new ArrayList<>(teamUserRoleMapping.keySet()))
                .stream()
                .map(u -> BeanMapper.map(u, TeamSimpleUserVO.class))
                .peek(u -> u.setUserRole(teamUserRoleMapping.get(u.getUserId())))
                .collect(toList());

        detail.setUsers(users);

        return detail;
    }
}
