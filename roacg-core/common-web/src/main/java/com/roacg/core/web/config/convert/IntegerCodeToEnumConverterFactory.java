package com.roacg.core.web.config.convert;

import com.roacg.core.model.enums.BaseCodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IntegerCodeToEnumConverterFactory implements ConverterFactory<Integer, BaseCodeEnum> {

    private static final Map<Class, Converter> CONVERTERS = new HashMap<>();

    /**
     * 获取一个从 Integer 转化为 T 的转换器，T 是一个泛型，有多个实现
     *
     * @param targetType 转换后的类型
     * @return 返回一个转化器
     */
    @Override
    public <T extends BaseCodeEnum> Converter<Integer, T> getConverter(Class<T> targetType) {
        Converter<Integer, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new IntegerToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }

    private class IntegerToEnumConverter<T extends BaseCodeEnum> implements Converter<Integer, T> {
        private Map<Integer, T> enumMap = new HashMap<>();

        public IntegerToEnumConverter(Class<T> enumType) {
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                enumMap.put(e.getCode(), e);
            }
        }

        @Override
        public T convert(Integer source) {
            T result = enumMap.get(source);
            if (Objects.isNull(result)) {
                throw new IllegalArgumentException("无法匹配对应的枚举类型 " + source);
            }
            return result;
        }
    }
}