package com.roacg.service.tc.flow.model.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;

public enum WorkflowDeliveryTagEnum implements JacksonSupportCodeEnum {

    NOT_STARTED(1, "未开始"),

    UNDELIVERED(2, "未交付"),

    DELIVERED(3, "已交付");

    private int code;

    private String desc;

    WorkflowDeliveryTagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }


    public static class Convert extends AbstractBaseCodeEnumConverter<WorkflowDeliveryTagEnum> {
        public Convert() {
            super(WorkflowDeliveryTagEnum.class);
        }
    }
}
