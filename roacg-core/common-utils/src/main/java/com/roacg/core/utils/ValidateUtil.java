package com.roacg.core.utils;


import javax.validation.Validator;
import java.util.Objects;

public final class ValidateUtil {

    private static volatile Validator validator;
    private static final Object LOCK = new Object();

    public static Validator getValidatorInstance() {
        if (Objects.isNull(validator)) {
            synchronized (LOCK) {
                if (Objects.isNull(validator)) {
                    validator = SpringContextUtil.getBean(Validator.class);
                }
            }
        }
        return validator;
    }


}
