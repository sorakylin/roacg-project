package com.roacg.service.tc.flow.model.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;

public enum DocumentTypeEnum implements JacksonSupportCodeEnum {

    DIR(1, "文件夹"),//Directory
    FILE(2, "文件夹");

    private int code;

    private String desc;

    DocumentTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }


    public static class Convert extends AbstractBaseCodeEnumConverter<DocumentTypeEnum> {
        public Convert() {
            super(DocumentTypeEnum.class);
        }
    }
}
