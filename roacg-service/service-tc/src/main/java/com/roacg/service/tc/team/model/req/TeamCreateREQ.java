package com.roacg.service.tc.team.model.req;

import com.roacg.service.tc.team.model.dto.TeamDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 用户创建团队的请求
 */
@Data
public class TeamCreateREQ {

    @NotBlank(message = "团队名称不能为空!")
    private String teamName;

    private String avatar;

    private String teamProfile;


    public TeamDTO transferToDTO() {
        TeamDTO dto = new TeamDTO();
        dto.setTeamName(teamName);
        dto.setAvatar(avatar);
        dto.setTeamProfile(teamProfile);
        return dto;
    }
}
