package com.roacg.service.tc.team.service;

import com.roacg.core.web.model.PageResponse;
import com.roacg.service.tc.team.model.dto.TeamDTO;
import com.roacg.service.tc.team.model.req.TeamCreateREQ;
import com.roacg.service.tc.team.model.req.TeamUpdateREQ;
import com.roacg.service.tc.team.model.vo.MyTeamsVO;

import java.util.List;
import java.util.Optional;

/**
 * 组服务
 */
public interface TeamService {

    //通过主键查询单个小组的信息
    Optional<TeamDTO> findTeamInfo(Long teamId);

    //分页批量查询和当前用户关联的小组
    PageResponse<MyTeamsVO> findMyTeams();

    //根据创建时间倒序, 分页查询小组
    List<TeamDTO> findTeamPageByCreateTime(int pageNum, int pageSize);

    //用户试图创建一个小组
    void createTeam(TeamCreateREQ r);

    //更新指定的小组
    void updateTeam(TeamUpdateREQ req);
}
