package com.roacg.core.model.resource;

import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;

import java.util.Objects;

/**
 * API 统一相应
 * Rest资源需要返回此对象
 *
 * @param <T> result data type
 */
public class RoApiResponse<T> {

    private static final long serialVersionUID = -1;


    private boolean success = true;

    /**
     * 具体响应代码
     *
     * @see RoApiStatusEnum
     */
    private int code = 0;

    private String msg;

    private T data;

    public RoApiResponse(RoApiStatusEnum status) {
        this(status.getCode(), null);
    }

    public RoApiResponse(int code) {
        this(code, null);
    }

    public RoApiResponse(RoApiStatusEnum status, String msg) {
        this(status.getCode(), msg);
    }

    public RoApiResponse(int code, String msg) {
        this(code, msg, null);
    }

    public RoApiResponse(RoApiStatusEnum status, String msg, T data) {
        this(status.getCode(), msg, data);
    }

    public RoApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;

        if (RoApiStatusEnum.SUCCESS.equals(code)) {
            this.success = true;
        } else {
            this.success = false;
        }
    }

    public static <T> RoApiResponse<T> ok() {
        return RoApiResponse.ok(null);
    }

    public static <T> RoApiResponse<T> ok(T data) {
        return RoApiResponse.ok(data, null);
    }

    public static <T> RoApiResponse<T> ok(T data, String msg) {
        return new RoApiResponse(RoApiStatusEnum.SUCCESS, msg, data);
    }


    public static <T> RoApiResponse<T> fail(RoApiStatusEnum status) {
        return RoApiResponse.fail(status, null);
    }

    public static <T> RoApiResponse<T> fail(RoApiStatusEnum status, String msg) {
        return RoApiResponse.fail(status, msg, null);
    }

    public static <T> RoApiResponse<T> fail(RoApiStatusEnum status, String msg, T data) {
        if (RoApiStatusEnum.SUCCESS.equals(status) || Objects.isNull(status)) {
            status = RoApiStatusEnum.SYSTEM_ERROR;
        }
        return new RoApiResponse(status, msg, data);
    }

    public static <T> RoApiResponse<T> fail(RoApiException e) {
        return (RoApiResponse<T>) RoApiResponse.fail(RoApiStatusEnum.forCode(e.getCode()).orElse(RoApiStatusEnum.SYSTEM_ERROR), e.getMsg(), e.getData());
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean getSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

}
