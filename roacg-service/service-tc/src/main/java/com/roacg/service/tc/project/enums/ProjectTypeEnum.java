package com.roacg.service.tc.project.enums;

import com.roacg.core.model.enums.BaseCodeEnum;
import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;

/**
 * 项目类型
 * 分为社区项目和团队项目
 */
public enum ProjectTypeEnum implements BaseCodeEnum<ProjectTypeEnum> {

    COMMUNITY(1),//社区项目
    TEAM(2);//团队项目

    private int code;

    ProjectTypeEnum(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public ProjectTypeEnum codeOf(int code) {
        return BaseCodeEnum.forCode(ProjectTypeEnum.class, code);
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<ProjectTypeEnum>{
        public Convert() {
            super(ProjectTypeEnum.class);
        }
    }
}
