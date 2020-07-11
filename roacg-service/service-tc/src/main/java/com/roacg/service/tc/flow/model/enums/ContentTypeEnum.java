package com.roacg.service.tc.flow.model.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;
import com.roacg.service.tc.flow.model.po.DocumentObjectPO;

/**
 * 文档的内容类型
 *
 * @see DocumentObjectPO#getContentType()
 */
public enum ContentTypeEnum implements JacksonSupportCodeEnum {

    OTHER(1, "其他"),
    TEXT(2, "文本"),
    LINK(3, "链接"),
    ANNEX(4, "附件");

    private int code;

    private String desc;

    ContentTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<ContentTypeEnum> {
        public Convert() {
            super(ContentTypeEnum.class);
        }
    }
}
