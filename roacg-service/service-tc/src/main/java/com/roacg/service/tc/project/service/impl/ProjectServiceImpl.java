package com.roacg.service.tc.project.service.impl;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.enums.DeletedStatusEnum;
import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.core.utils.context.RoContext;
import com.roacg.service.tc.project.enums.ProjectPermissionStatusEnum;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import com.roacg.service.tc.project.model.dto.SimpleProjectDTO;
import com.roacg.service.tc.project.model.po.ProjectPO;
import com.roacg.service.tc.project.model.po.ProjectUserPO;
import com.roacg.service.tc.project.model.req.ProjectCreateREQ;
import com.roacg.service.tc.project.repository.ProjectRepository;
import com.roacg.service.tc.project.repository.ProjectUserRepository;
import com.roacg.service.tc.project.service.ProjectService;
import com.roacg.service.tc.team.enums.UserTeamRoleEnum;
import com.roacg.service.tc.team.model.po.TeamUserPO;
import com.roacg.service.tc.team.repository.TeamUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;


@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Override
    public Optional<SimpleProjectDTO> findSimpleProject(Long projectId) {
        if (Objects.isNull(projectId)) return Optional.empty();

        return projectRepository.findById(projectId)
                .map(p -> BeanMapper.map(p, SimpleProjectDTO.class));
    }

    @Override
    public List<SimpleProjectDTO> findSimpleProject(Collection<Long> projectId) {

        if (CollectionUtils.isEmpty(projectId)) {
            return Collections.emptyList();
        }

        List<ProjectPO> projects = projectRepository.findAllById(projectId);

        if (CollectionUtils.isEmpty(projects)) {
            return Collections.emptyList();
        }

        List<SimpleProjectDTO> result = projects
                .stream()
                .map(p -> BeanMapper.map(p, SimpleProjectDTO.class))
                .collect(toList());
        return result;
    }

    @Override
    public List<SimpleProjectDTO> findTeamSimpleProject(Long teamId) {
        if (Objects.isNull(teamId)) return Collections.emptyList();

        //查出该团队的所有项目
        List<SimpleProjectDTO> projects = projectRepository.findAllByTeamId(teamId)
                .stream()
                .map(p -> BeanMapper.map(p, SimpleProjectDTO.class))
                .collect(toList());

        if (projects.isEmpty()) return projects;

        //根据当前用户过滤, 只显示当前用户可以访问的
        RequestUser user = RoContext.getRequestUser();

        //基础判断设置
        Predicate<SimpleProjectDTO> allPublic = (project) -> project.getProjectPermissionStatus() == ProjectPermissionStatusEnum.ALL_PUBLIC;
        Predicate<SimpleProjectDTO> infoPublic = (project) -> project.getProjectPermissionStatus() == ProjectPermissionStatusEnum.INFO_PUBLIC;
//        Predicate<SimpleProjectDTO> teamPublic = (project) -> project.getProjectPermissionStatus() == ProjectPermissionStatusEnum.TEAM_PUBLIC;
//        Predicate<SimpleProjectDTO> infoTeamPublic = (project) -> project.getProjectPermissionStatus() == ProjectPermissionStatusEnum.INFO_TEAM_PUBLIC;
        Predicate<SimpleProjectDTO> onlyParticipant = (project) -> project.getProjectPermissionStatus() == ProjectPermissionStatusEnum.ONLY_PARTICIPANT;

        if (!user.hasLogin()) {
            return projects.stream().filter(allPublic.or(infoPublic)).collect(toList());
        }

        List<TeamUserPO> teamUsers = teamUserRepository.findAllByTeamId(teamId);
        Optional<TeamUserPO> teamUser = teamUsers.stream().filter(tu -> Objects.equals(tu.getUserId(), user.getId())).findFirst();

        //不是组内成员
        if (teamUser.isEmpty()) {
            return projects.stream().filter(allPublic.or(infoPublic)).collect(toList());
        }

        UserTeamRoleEnum userTeamRole = teamUser.map(TeamUserPO::getUserTeamRole).get();
        //组长和元老可以直接查看所有
        if (UserTeamRoleEnum.TEAM_LEADER == userTeamRole || UserTeamRoleEnum.OLD_HEAD == userTeamRole) {
            return projects;
        }

        //分个组, true是仅参加者能查看的项目, false是其他的
        Map<Boolean, List<SimpleProjectDTO>> group = projects.stream().collect(partitioningBy(onlyParticipant));
        List<SimpleProjectDTO> onlyParticipantProject = group.get(Boolean.TRUE);
        projects = group.getOrDefault(Boolean.FALSE, new ArrayList<>());

        if (CollectionUtils.isEmpty(onlyParticipantProject)) {
            return projects;
        }

        //查出限制为【仅参加者】权限的项目后分个组, key: projectId,value: userIds
        Map<Long, List<Long>> projectUserMapping = onlyParticipantProject.stream().map(SimpleProjectDTO::getProjectId)
                .collect(collectingAndThen(toList(), projectUserRepository::findByProjectIdIn))
                .stream()
                .collect(groupingBy(ProjectUserPO::getProjectId, mapping(ProjectUserPO::getUserId, toList())));

        //找出当前登陆用户可以访问的项目然后设置到返回值中
        onlyParticipantProject.stream()
                .filter(p -> {
                    List<Long> userIds = projectUserMapping.get(p.getProjectId());
                    return !CollectionUtils.isEmpty(userIds) && userIds.contains(user.getId());
                }).collect(collectingAndThen(toList(), projects::addAll));

        return projects;
    }

    @Override
    public List<Long> findProjectUserIds(Long projectId) {
        if (Objects.isNull(projectId)) return Collections.emptyList();
        return projectUserRepository.findUserIdByProjectId(projectId);
    }

    @Override
    @Transactional
    public void createProject(ProjectCreateREQ req) {

        //检查重名项目
        if (req.getTeamId() != null) {
            int count = projectRepository.countByProjectNameAndTeamIdAndDeleted(req.getProjectName(), req.getTeamId(), DeletedStatusEnum.UN_DELETE);
            if (count > 0) throw new ParameterValidationException("团队内已有相同名字的项目! 项目名:" + req.getProjectName());
        } else {
            int count = projectRepository.countByProjectNameAndProjectType(req.getProjectName(), ProjectTypeEnum.COMMUNITY);
            if (count > 0) throw new ParameterValidationException("已有相同名字的项目! 项目名:" + req.getProjectName());
        }

        ProjectPO entity = BeanMapper.map(req, ProjectPO.class);

        RequestUser requestUser = RoContext.getRequestUser();

        entity.setDeleted(DeletedStatusEnum.UN_DELETE);
        entity.setFounderId(requestUser.getId());
        projectRepository.saveAndFlush(entity);

        projectUserRepository.save(ProjectUserPO.newInstance(entity.getProjectId(), requestUser.getId()));

    }

    @Override
    @Transactional
    public void bindUsers(Long projectId, Long... userIds) {

        if (Objects.isNull(projectId) || ArrayUtils.isEmpty(userIds)) return;

        // 检查是否存在项目
        Optional<ProjectPO> project = projectRepository.findById(projectId);
        if (project.isEmpty()) return;

        //项目创建者才能拉人, 其他的只能申请
        project.map(ProjectPO::getFounderId)
                .filter(RoContext.getRequestUser().getId()::equals)
                .orElseThrow(() -> new ParameterValidationException("只有项目创建者才能拉人"));

        //检查人员是否为小组中人/项目中人
        List<Long> projectUsers = projectUserRepository.findUserIdByProjectId(projectId);
        List<Long> teamUsers = project.map(ProjectPO::getTeamId)
                .map(teamUserRepository::findUserIdsByTeamId)
                .orElse(List.of());

        List<Long> bindIds = Arrays.stream(userIds)
                .filter(uid -> !projectUsers.contains(uid))
                .filter(uid -> !teamUsers.contains(uid))
                .collect(toList());

        if (bindIds.isEmpty()) return;

        //开始设置绑定关系
        List<ProjectUserPO> bindEntity = bindIds.stream().map(uid -> {
            ProjectUserPO saveEntity = new ProjectUserPO();
            saveEntity.setProjectId(projectId);
            saveEntity.setUserId(uid);
            return saveEntity;
        }).collect(toList());

        projectUserRepository.saveAll(bindEntity);
    }
}
