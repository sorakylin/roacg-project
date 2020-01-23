package com.roacg.core.model.exception;

import com.roacg.core.model.enums.RoApiStatusEnum;

/**
 * 系统内统一异常
 */
public class RoApiException extends RuntimeException {

    /**
     * 异常代码
     * 默认为系统异常，系统异常表示是代码逻辑问题，需要进行代码逻辑处理
     * 一个正常的系统中不应该出现此异常
     *
     * @see com.roacg.core.model.enums.RoApiStatusEnum
     */
    private int code = RoApiStatusEnum.SYSTEM_ERROR.getCode();

    private String msg;

    private Object data;

    public RoApiException(RoApiStatusEnum status) {
        this(status, null);
    }

    public RoApiException(RoApiStatusEnum status, String msg) {
        this(status, msg, null);
    }

    public RoApiException(RoApiStatusEnum status, String msg, Object data) {
        if (!RoApiStatusEnum.SUCCESS.valueIs(code)) {
            this.code = code;
        }
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
