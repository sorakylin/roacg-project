package com.roacg.service.tc.project.enums;

import com.roacg.core.model.enums.BaseCodeEnum;
import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;

/**
 * 项目内容的隐私状态
 * 针对于 <b>项目</b> 本体
 */
public enum ProjectPermissionStatusEnum implements BaseCodeEnum {

    ALL_PUBLIC(1),//全公开的 (项目信息和具体文档)
    INFO_PUBLIC(2),//信息是公开的 (只能查看项目信息, 不能看到文档)
    TEAM_PUBLIC(3),//团队内全公开 (团队成员可以查看所有)
    INFO_TEAM_PUBLIC(4),//只在团队内公开信息 (团队成员只能查看项目信息)
    ONLY_PARTICIPANT(5); //仅参加者 (项目信息和具体文档只有项目参与者能看见)

    private int code;

    ProjectPermissionStatusEnum(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<ProjectPermissionStatusEnum> {
        public Convert() {
            super(ProjectPermissionStatusEnum.class);
        }
    }
}
