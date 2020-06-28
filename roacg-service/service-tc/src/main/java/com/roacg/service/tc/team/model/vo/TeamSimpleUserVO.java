package com.roacg.service.tc.team.model.vo;

import lombok.Data;

@Data
public class TeamSimpleUserVO {

    private Long userId;

    private String userName;

    private String avatar;

    private Integer userRole;
}
