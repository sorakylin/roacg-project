package com.roacg.service.tc.project.resource;


import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.exception.ParameterValidationException;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.project.model.vo.ProjectDetailVO;
import com.roacg.service.tc.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/project")
@ExposeResource
public class ProjectResource {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public RoApiResponse findProjectDetail(@RequestParam("pid") Long pid) {
        Optional<ProjectDetailVO> result = projectService.findProjectDetail(pid);
        return result.map(RoApiResponse::ok).orElseThrow(() -> new ParameterValidationException("无此项目"));
    }

    /**
     * 给项目拉人,  可以直接将用户拉到本项目里边来 (TODO 要经过同意么? )
     * 如果项目是属于一个团队的 , 则只能拉团队里的人进项目
     *
     * @param id      项目ID
     * @param userIds 要绑定的用户ID
     */
    @PostMapping("/{id}")
    @ExposeResource(type = PermissionType.LOGIN)
    public RoApiResponse bindUsersToProject(@PathVariable Long id, @RequestBody Long[] userIds) {
        projectService.bindUsers(id, userIds);
        return RoApiResponse.ok();
    }
}
