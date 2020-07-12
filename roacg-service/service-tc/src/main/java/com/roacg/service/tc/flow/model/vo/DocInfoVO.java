package com.roacg.service.tc.flow.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.roacg.core.utils.serialize.LocalDateTimeSerializer;
import com.roacg.service.tc.flow.model.enums.ContentTypeEnum;
import com.roacg.service.tc.flow.model.enums.DocumentStateEnum;
import com.roacg.service.tc.flow.model.enums.DocumentTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocInfoVO {

    @JsonSerialize(using= ToStringSerializer.class)
    private Long documentId;

    //文档名, 同一层下不能重名
    private String documentName;

    //文档类型, 文件夹 or 文件
    private DocumentTypeEnum documentType;

    //内容类型, 文本? 链接? 附件?
    private ContentTypeEnum contentType;

    //文档状态 未开始/进行中/待验收/已完成
    private DocumentStateEnum documentState;

    private Integer size;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;
}
