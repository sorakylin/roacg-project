package com.roacg.service.tc.project.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;

/**
 * 项目类型
 * 分为社区项目和团队私有项目
 * 这决定了一个项目是否可以被除团队之外的人访问
 */
public enum ProjectTypeEnum implements JacksonSupportCodeEnum {

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

    public static class Convert extends AbstractBaseCodeEnumConverter<ProjectTypeEnum> {
        public Convert() {
            super(ProjectTypeEnum.class);
        }
    }
}
