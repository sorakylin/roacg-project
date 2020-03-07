package com.roacg.core.model.enums;

import java.util.Objects;

/**
 * 删除标志
 * 表示一张表内某条数据的删除与否
 * 一般用于做软删除相关操作/判定时使用
 */
public enum DeletedStatusEnum implements BaseCodeEnum {

    UN_DELETE(0), DELETED(1);

    private int value;

    DeletedStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean valueIs(int value) {
        return Objects.equals(this.value, value);
    }


    @Override
    public int getCode() {
        return getValue();
    }
}
