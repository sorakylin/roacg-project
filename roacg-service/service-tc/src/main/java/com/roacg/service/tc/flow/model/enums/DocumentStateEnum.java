package com.roacg.service.tc.flow.model.enums;

import com.roacg.core.model.enums.convert.AbstractBaseCodeEnumConverter;
import com.roacg.core.web.model.enums.JacksonSupportCodeEnum;
import com.roacg.service.tc.flow.model.po.DocumentObjectPO;

/**
 * 文档状态
 *
 * @see DocumentObjectPO#getDocumentState()
 */
public enum DocumentStateEnum implements JacksonSupportCodeEnum {

    NOT_STARTED(1, "未开始"),
    IN_TRANSLATION(2, "翻译中"),
    PENDING_ACCEPTANCE(3, "待验收"),
    COMPLETED(4, "已完成");

    private int code;

    private String desc;

    DocumentStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static class Convert extends AbstractBaseCodeEnumConverter<DocumentStateEnum> {
        public Convert() {
            super(DocumentStateEnum.class);
        }
    }
}
