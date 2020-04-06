package com.roacg.service.tc.team.model.vo;

import com.roacg.service.tc.team.model.dto.TeamDTO;
import lombok.Data;

@Data
public class ActiveTeamVO {

    private Long teamId;

    private String teamName;

    private String avatar;

    //团队简介
    private String teamProfile;

    //团队当前人数
    private Integer teamSize;

    //团队已创建的项目数量
    private Integer projectNum;


    public static ActiveTeamVO from(TeamDTO dto){
        ActiveTeamVO activeTeamVO = new ActiveTeamVO();
        activeTeamVO.setTeamId(dto.getTeamId());
        activeTeamVO.setTeamName(dto.getTeamName());
        activeTeamVO.setAvatar(dto.getAvatar());
        activeTeamVO.setTeamProfile(dto.getTeamProfile());
        activeTeamVO.setTeamSize(dto.getTeamSize());
        activeTeamVO.setProjectNum(dto.getProjectNum());
        return activeTeamVO;
    }
}
