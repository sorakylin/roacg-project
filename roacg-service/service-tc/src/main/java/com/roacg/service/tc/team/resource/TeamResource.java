package com.roacg.service.tc.team.resource;

import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.po.TeamPO;
import com.roacg.service.tc.team.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.roacg.core.model.resource.RoApiResponse.*;

@RestController
@RequestMapping("/team")
@AllArgsConstructor
public class TeamResource {

    private TeamService teamService;

    /**
     * 查询一个项目组的描述
     *
     *
     * @see TeamPO#getTeamDescription()
     * @param teamId
     * @return
     */
    @RequestMapping(value = "/description", method = RequestMethod.GET)
    public RoApiResponse<String> findTeamDescription(@RequestParam("teamId") Long teamId) {
        return teamService.findTeamInfo(teamId)
                .map(TeamDTO::getTeamDescription)
                .map(desc -> ok(desc))
                .orElse(ok());
    }


}
