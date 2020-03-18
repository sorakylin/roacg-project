package com.roacg.service.tc.team.resource;

import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/team")
@AllArgsConstructor
public class TeamResource {

    private TeamService teamService;

    @RequestMapping(value = "/description", method = RequestMethod.GET)
    public RoApiResponse<String> findTeamDescription(@RequestParam("teamId") Long teamId) {
        return teamService.findTeamInfo(teamId)
                .map(TeamDTO::getTeamDescription)
                .map(desc -> RoApiResponse.ok(desc))
                .orElse(RoApiResponse.ok());
    }


}
