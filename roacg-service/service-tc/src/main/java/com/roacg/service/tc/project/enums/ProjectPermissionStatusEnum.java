package com.roacg.service.tc.project.enums;

import com.roacg.core.model.enums.BaseCodeEnum;
import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;

/**
 * 项目的隐私状态
 * 针对于 <b>项目</b> 本体
 */
public enum ProjectPermissionStatusEnum implements BaseCodeEnum<ProjectPermissionStatusEnum> {

    PUBLIC(1),//公开的
    TEAM_PUBLIC(2),//团队内公开
    ONLY_PARTICIPANT(3); //仅参加者

    private int code;

    ProjectPermissionStatusEnum(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public ProjectPermissionStatusEnum codeOf(int code) {
        return BaseCodeEnum.forCode(ProjectPermissionStatusEnum.class, code);
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<ProjectPermissionStatusEnum> {
        public Convert() {
            super(ProjectPermissionStatusEnum.class);
        }
    }
}
