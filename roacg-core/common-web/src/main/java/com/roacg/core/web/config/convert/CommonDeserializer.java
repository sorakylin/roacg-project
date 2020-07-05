package com.roacg.core.web.config.convert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.roacg.core.model.enums.BaseCodeEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 用来在 jackson 里反序列化枚举的
 */
@Data
public class CommonDeserializer extends JsonDeserializer<BaseCodeEnum> implements ContextualDeserializer {

    private Class clz;

    @Override
    public BaseCodeEnum deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        if (StringUtils.isEmpty(jsonParser.getText())) {
            return null;
        }


        if (BaseCodeEnum.class.isAssignableFrom(clz)) {
            return (BaseCodeEnum) BaseCodeEnum.forCode(clz, jsonParser.getIntValue());
        } else {
            return null;
        }
    }

    /**
     * 获取合适的解析器，把当前解析的属性Class对象存起来，以便反序列化的转换类型，为了避免线程安全问题，每次都new一个
     *
     * @param ctx
     * @param property
     * @return
     * @throws JsonMappingException
     */
    public JsonDeserializer createContextual(DeserializationContext ctx, BeanProperty property) throws JsonMappingException {
        Class rawCls = ctx.getContextualType().getRawClass();
        CommonDeserializer clone = new CommonDeserializer();
        clone.setClz(rawCls);
        return clone;
    }
}