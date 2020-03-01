package com.roacg.service.system.security.exception;

import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;

public class RoSecurityAuthException extends RoApiException {

    public RoSecurityAuthException() {
        super(RoApiStatusEnum.UNAUTHORIZED, "Unauthorized");
    }

    public RoSecurityAuthException(String msg) {
        super(RoApiStatusEnum.UNAUTHORIZED, msg);
    }


    public static void throwException() {
        throw new RoSecurityAuthException();
    }

    public static void throwException(String msg) {
        throw new RoSecurityAuthException(msg);
    }

    public static RoSecurityAuthException newException() {
        return new RoSecurityAuthException();
    }

    public static RoSecurityAuthException newException(String msg) {
        return new RoSecurityAuthException(msg);
    }


}
