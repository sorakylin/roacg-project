package com.roacg.service.tc.team.resource;

import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.po.TeamPO;
import com.roacg.service.tc.team.model.req.TeamCreateREQ;
import com.roacg.service.tc.team.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.roacg.core.model.resource.RoApiResponse.ok;

@RestController
@RequestMapping("/team")
@AllArgsConstructor
public class TeamResource {

    private TeamService teamService;

    /**
     * 查询一个项目组的描述
     *
     * @param teamId
     * @return
     * @see TeamPO#getTeamDescription()
     */
    @GetMapping(value = "/description")
    @ExposeResource
    public RoApiResponse<String> findTeamDescription(@RequestParam("teamId") Long teamId) {
        return teamService.findTeamInfo(teamId)
                .map(TeamDTO::getTeamDescription)
                .map(desc -> ok(desc))
                .orElse(ok());
    }

    /**
     * 用户试图创建一个小组
     *
     * @param req 小组的内容
     * @return 200 == 成功
     */
    @PostMapping
    @ExposeResource(type = PermissionType.LOGIN)
    public RoApiResponse createTeam(@RequestBody TeamCreateREQ req) {
        teamService.createTeam(req);
        return RoApiResponse.ok();
    }
}
