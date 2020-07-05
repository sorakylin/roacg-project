package com.roacg.service.tc.project.resource;

import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.project.model.dto.SimpleProjectDTO;
import com.roacg.service.tc.project.model.req.ProjectCreateREQ;
import com.roacg.service.tc.project.model.vo.ProjectVO;
import com.roacg.service.tc.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/t-project")
public class TeamProjectResource {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @ExposeResource(type = PermissionType.LOGIN)
    public RoApiResponse createProject(@RequestBody ProjectCreateREQ req) {
        projectService.createProject(req);
        return RoApiResponse.ok();
    }

    /**
     * 查询指定小组下的项目, 根据当前请求用户的权限不同而返回不同的结果
     *
     * @param teamId 团队ID
     * @return 此团队的项目们
     */
    @GetMapping("/{teamId}")
    @ExposeResource(type = PermissionType.PUBLIC)
    public RoApiResponse createProject(@PathVariable Long teamId) {
        List<SimpleProjectDTO> resultDTO = projectService.findTeamSimpleProject(teamId);

        return resultDTO.stream()
                .map(p -> BeanMapper.map(p, ProjectVO.class))
                .collect(Collectors.collectingAndThen(Collectors.toList(), RoApiResponse::ok));
    }


}
