package com.roacg.service.tc.project.enums;

import com.roacg.core.model.enums.BaseCodeEnum;
import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import lombok.Getter;

/**
 * 标识一个项目的状态
 */
@Getter
public enum ProjectStatusEnum implements BaseCodeEnum<ProjectStatusEnum> {

    NOT_STARTED(1, "未开始"),
    PROCESSING(2, "进行中"),
    COMPLETED(3, "已完成"),
    ABORTED(4, "已中止");

    private int code;

    private String desc;

    ProjectStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public ProjectStatusEnum codeOf(int code) {
        return BaseCodeEnum.forCode(ProjectStatusEnum.class, code);
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<ProjectStatusEnum> {
        public Convert() {
            super(ProjectStatusEnum.class);
        }
    }
}
