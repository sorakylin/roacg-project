package com.roacg.core.model.exception;

import com.roacg.core.model.enums.RoApiStatusEnum;

/**
 * 参数效验异常
 * 一般用于API效验参数的时候抛出
 */
public class ParameterValidationException extends RoApiException {

    public ParameterValidationException() {
        super(RoApiStatusEnum.ILLEGAL_PARAM,"参数效验失败! 请检查入参有效性");
    }

    public ParameterValidationException(String message) {
        super(RoApiStatusEnum.ILLEGAL_PARAM,message);
    }
}
