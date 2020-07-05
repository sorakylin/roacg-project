package com.roacg.core.model.enums;

import java.util.stream.Stream;

public interface BaseCodeEnum {

    int getCode();

    static <E extends Enum<E> & BaseCodeEnum> E forCode(Class<E> cls, int code) {
        return Stream.of(cls.getEnumConstants())
                .filter(e -> e.getCode() == code)
                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Unknown code '" + code + "' for enum " + cls.getName()));
                .orElse(null);
    }

}