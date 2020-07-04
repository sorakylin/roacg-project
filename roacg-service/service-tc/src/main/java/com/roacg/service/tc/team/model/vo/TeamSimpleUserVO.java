package com.roacg.service.tc.team.model.vo;

import lombok.Data;

@Data
public class TeamSimpleUserVO {

    private Long userId;

    private String userName;

    private String avatar;

    /**
     * 用户在小组内的角色
     *
     * @see com.roacg.service.tc.team.enums.UserTeamRoleEnum
     */
    private Integer userRole;
}
