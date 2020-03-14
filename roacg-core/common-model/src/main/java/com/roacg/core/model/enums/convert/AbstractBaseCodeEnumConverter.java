package com.roacg.core.model.enums.convert;

import com.roacg.core.model.enums.BaseCodeEnum;

import javax.persistence.AttributeConverter;

/**
 * 抽象的枚举转换器
 * 只要是 BaseCodeEnum 此接口的实现类，只需继承此类则可以实现 enum/code 的转换
 * 一般是用于 {@link javax.persistence.Convert,javax.persistence.Column} 所标识的枚举
 * 在 持久化/RS反序列化 时进行转化用
 *
 * @param <E>
 */
public abstract class AbstractBaseCodeEnumConverter<E extends Enum<E> & BaseCodeEnum>
        implements AttributeConverter<E, Integer> {

    private Class<E> codeEnumClass;

    public AbstractBaseCodeEnumConverter(Class<E> codeEnumClass) {
        this.codeEnumClass = codeEnumClass;
    }

    @Override
    public Integer convertToDatabaseColumn(E attr) {
        return (attr == null)
                ? null
                : attr.getCode();
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        return (dbData == null)
                ? null
                : BaseCodeEnum.forCode(this.codeEnumClass, dbData);
    }

}