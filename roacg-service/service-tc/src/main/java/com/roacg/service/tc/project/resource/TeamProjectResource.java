package com.roacg.service.tc.project.resource;

import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.project.model.req.ProjectCreateREQ;
import com.roacg.service.tc.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
