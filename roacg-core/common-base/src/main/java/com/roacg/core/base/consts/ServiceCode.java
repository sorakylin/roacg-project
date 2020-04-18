package com.roacg.core.base.consts;


/**
 * 不同微服务之间的代号
 */
public enum ServiceCode {

    GATEWAY("g"),
    SYSTEM("s"),
    //Translation collaboration
    TC("tc");


    private String code;

    ServiceCode(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
