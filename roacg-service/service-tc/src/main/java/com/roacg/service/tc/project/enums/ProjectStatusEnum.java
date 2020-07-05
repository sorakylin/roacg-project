package com.roacg.service.tc.project.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;
import lombok.Getter;

/**
 * 标识一个项目的状态
 */
@Getter
public enum ProjectStatusEnum implements JacksonSupportCodeEnum {

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

    public static class Convert extends AbstractBaseCodeEnumConverter<ProjectStatusEnum> {
        public Convert() {
            super(ProjectStatusEnum.class);
        }
    }
}
