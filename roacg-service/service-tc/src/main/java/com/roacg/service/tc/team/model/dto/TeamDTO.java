package com.roacg.service.tc.team.model.dto;

import com.roacg.service.tc.team.model.po.TeamPO;
import lombok.Data;

import java.io.Serializable;

@Data
public class TeamDTO implements Serializable {

    private Long teamId;

    private String teamName;

    private String avatar;

    //团队简介
    private String teamProfile;

    //团队说明
    private String teamDescription;

    //团队等级,根据等级 相应的权限也有所不同
    private Integer teamGrade;

    //团队当前人数
    private Integer teamSize;

    //团队已创建的项目数量
    private Integer projectNum;

    public static TeamDTO from(TeamPO teamPO) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamId(teamPO.getTeamId());
        teamDTO.setTeamName(teamPO.getTeamName());
        teamDTO.setTeamProfile(teamPO.getTeamProfile());
        teamDTO.setTeamDescription(teamPO.getTeamDescription());
        teamDTO.setTeamGrade(teamPO.getTeamGrade());
        teamDTO.setTeamSize(teamPO.getTeamSize());
        teamDTO.setProjectNum(teamPO.getProjectNum());
        return teamDTO;
    }
}
