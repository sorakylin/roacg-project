package com.roacg.service.tc.project.service.impl;

import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.service.tc.project.model.dto.SimpleProjectDTO;
import com.roacg.service.tc.project.model.po.ProjectPO;
import com.roacg.service.tc.project.repository.ProjectRepository;
import com.roacg.service.tc.project.repository.ProjectUserRepository;
import com.roacg.service.tc.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectRepository projectRepository;


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
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public List<SimpleProjectDTO> findTeamSimpleProject(Long teamId) {
        if (Objects.isNull(teamId)) return Collections.emptyList();

        return projectRepository.findAllByTeamId(teamId)
                .stream()
                .map(p -> BeanMapper.map(p, SimpleProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findProjectUserIds(Long projectId) {
        if (Objects.isNull(projectId)) return Collections.emptyList();
        return projectUserRepository.findUserIdByProjectId(projectId);
    }
}
