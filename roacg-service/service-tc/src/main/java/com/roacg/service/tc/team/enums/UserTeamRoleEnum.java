package com.roacg.service.tc.team.enums;

import com.roacg.core.model.enums.BaseCodeEnum;
import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.service.tc.team.model.po.TeamUserPO;

/**
 * 表示一个角色在某个小组中的角色
 *
 * @see TeamUserPO#getUserTeamRole
 */
public enum UserTeamRoleEnum implements BaseCodeEnum<UserTeamRoleEnum> {

    TEAM_LEADER(1),//组长
    OLD_HEAD(2),//元老
    TEAM_MEMBER(3);//普通组员

    private int code;

    UserTeamRoleEnum(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public UserTeamRoleEnum codeOf(int code) {
        return BaseCodeEnum.forCode(UserTeamRoleEnum.class, code);
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<UserTeamRoleEnum> {

        public Convert() {
            super(UserTeamRoleEnum.class);
        }
    }
}
