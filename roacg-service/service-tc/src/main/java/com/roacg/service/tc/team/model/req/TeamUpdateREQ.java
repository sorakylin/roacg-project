package com.roacg.service.tc.team.model.req;

import com.roacg.service.tc.team.model.dto.TeamDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 用户创建团队的请求
 */
@Data
public class TeamUpdateREQ {

    @NotNull(message = "要修改的团队不能为空!")
    private Long teamId;

    @NotBlank(message = "要修改的团队不能为空!")
    private String teamName;

    //头像
    private String avatar;

    //简介
    @Length(max = 85, message = "团队简介长度过长!")
    private String teamProfile;

    //说明
    @Length(max = 20000, message = "团队说明过长!")
    private String teamDescription;


    public TeamDTO transferToDTO() {
        TeamDTO dto = new TeamDTO();
        dto.setTeamId(teamId);
        dto.setTeamName(teamName);
        dto.setAvatar(avatar);
        dto.setTeamProfile(teamProfile);
        dto.setTeamDescription(teamDescription);
        return dto;
    }
}
