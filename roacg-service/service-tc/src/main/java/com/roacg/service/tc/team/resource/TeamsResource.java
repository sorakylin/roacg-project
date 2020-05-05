package com.roacg.service.tc.team.resource;

import com.roacg.core.model.auth.enmus.PermissionType;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.web.security.annotation.ExposeResource;
import com.roacg.service.tc.team.model.dto.TeamDTO;
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
    @ExposeResource
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
    @ExposeResource
    public RoApiResponse<List<ActiveTeamVO>> findNewlyCreatedTeams() {
        return teamService.findTeamPageByCreateTime(1, 5)
                .stream()
                .map(ActiveTeamVO::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), RoApiResponse::ok));
    }

    /**
     * 查找当前登录用户的团队
     * 包括当前用户建立的和加入的
     * 排序为用户处于团队中的角色等级高低来排
     *
     * @see com.roacg.service.tc.team.enums.UserTeamRoleEnum
     */
    @GetMapping("/my")
    @ExposeResource(type = PermissionType.LOGIN)
    public RoApiResponse findMyTeams() {
        List<TeamDTO> result = teamService.findMyTeams();
        return RoApiResponse.ok(result);
    }
}
