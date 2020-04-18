package com.roacg.service.tc.team.resource;

import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.team.model.vo.ActiveTeamVO;
import com.roacg.service.tc.team.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tms")
@AllArgsConstructor
@ExposeResource
public class TeamsResource {

    private TeamService teamService;

    /**
     * 查询最近活跃的项目组们
     *
     * @return 最活跃的五个团队
     */
    @GetMapping("/active-most")
    public RoApiResponse<?> findMostActiveTeams() {
        //TODO 待实现
        return RoApiResponse.ok(Collections.emptyList());
    }


    /**
     * 查询最新创建的的团队们
     *
     * @return 最新创建的的五个项目组
     */
    @GetMapping("/newly-created")
    public RoApiResponse<List<ActiveTeamVO>> findNewlyCreatedTeams() {
        return teamService.findTeamPageByCreateTime(1, 5)
                .stream()
                .map(ActiveTeamVO::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), RoApiResponse::ok));
    }

}
