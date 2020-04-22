package com.roacg.service.tc.team.service;

import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.req.TeamCreateREQ;

import java.util.List;
import java.util.Optional;

/**
 * 组服务
 */
public interface TeamService {

    //通过主键查询单个小组的信息
    Optional<TeamDTO> findTeamInfo(Long teamId);

    //根据创建时间倒序, 分页查询小组
    List<TeamDTO> findTeamPageByCreateTime(int pageNum, int pageSize);

    //用户试图创建一个小组
    void createTeam(TeamCreateREQ req);
}
