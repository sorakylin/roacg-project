package com.roacg.core.web.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.roacg.core.model.enums.BaseCodeEnum;
import com.roacg.core.web.config.convert.CommonDeserializer;

/**
 * 支持Web传输的枚举
 * 用于兼容 jackson 序列化/反序列话
 */
@JsonDeserialize(using = CommonDeserializer.class)
public interface JacksonSupportCodeEnum extends BaseCodeEnum {

    @JsonValue
    int getCode();
}