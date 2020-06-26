package com.roacg.service.tc.team.model.vo;

import com.roacg.service.tc.team.enums.UserTeamRoleEnum;
import lombok.Data;

/**
 * 我的团队
 */
@Data
public class MyTeamsVO {

    private Long teamId;

    private String teamName;

    //头像
    private String avatar;

    //团队简介 varchar最多85汉字
    private String teamProfile;

    //团队当前人数
    private Integer teamSize;

    //团队已创建的项目数
    private Integer projectNum;

    /**
     * 我的团队角色
     *
     * @see UserTeamRoleEnum
     */
    private Integer myRole;
}
