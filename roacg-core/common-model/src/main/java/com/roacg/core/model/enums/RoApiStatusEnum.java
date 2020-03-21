package com.roacg.core.model.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 接口返回响应的状态枚举
 */
public enum RoApiStatusEnum {

    SUCCESS(0),//成功
    TIME_OUT(30),//超时
    SYSTEM_ERROR(50),//系统内部错误
    ILLEGAL_VERSION(51),//非法版本
    UNAUTHORIZED(401),//未经认证
    FORBIDDEN(403), //  未经授权
    ILLEGAL_PARAM(614);//非法参数

    private int code;

    RoApiStatusEnum(int code) {
        this.code = code;
    }

    public boolean valueIs(int code) {
        return Objects.equals(this.code, code);
    }

    public static Optional<RoApiStatusEnum> forCode(int code) {
        return Arrays.stream(RoApiStatusEnum.values()).filter(en -> en.valueIs(code)).findFirst();
    }

    public int getCode() {
        return code;
    }
}
