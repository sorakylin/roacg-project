package com.roacg.service.tc.project.service.impl;

import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.enums.DeletedStatusEnum;
import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.core.utils.context.RoContext;
import com.roacg.service.tc.project.enums.ProjectTypeEnum;
import com.roacg.service.tc.project.model.dto.SimpleProjectDTO;
import com.roacg.service.tc.project.model.po.ProjectPO;
import com.roacg.service.tc.project.model.po.ProjectUserPO;
import com.roacg.service.tc.project.model.req.ProjectCreateREQ;
import com.roacg.service.tc.project.repository.ProjectRepository;
import com.roacg.service.tc.project.repository.ProjectUserRepository;
import com.roacg.service.tc.project.service.ProjectService;
import com.roacg.service.tc.team.repository.TeamUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;


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

        return projectRepository.findAllByTeamId(teamId)
                .stream()
                .map(p -> BeanMapper.map(p, SimpleProjectDTO.class))
                .collect(toList());
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
