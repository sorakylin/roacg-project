package com.roacg.core.web.config.convert;

import com.roacg.core.model.enums.BaseCodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.stream.Stream;

public class CodeEnumConverterFactory implements ConverterFactory<Integer, BaseCodeEnum> {

    @Override
    public <E extends BaseCodeEnum> Converter<Integer, E> getConverter(Class<E> targetType) {
        return (Integer source) -> Stream.of(targetType.getEnumConstants())
                .filter(e -> e.getCode() == source)
                .findFirst()
                .orElse(null);
    }
}