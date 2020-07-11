package com.roacg.service.tc.flow.model.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;

public enum TranslateResponsibilitiesEnum implements JacksonSupportCodeEnum {

    TRANSLATION(1, "翻译"),
    PROOFREADING(2, "校对"),
    POLISH(3, "润色");

    private int code;

    private String desc;

    TranslateResponsibilitiesEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }


    public static class Convert extends AbstractBaseCodeEnumConverter<TranslateResponsibilitiesEnum> {
        public Convert() {
            super(TranslateResponsibilitiesEnum.class);
        }
    }
}
