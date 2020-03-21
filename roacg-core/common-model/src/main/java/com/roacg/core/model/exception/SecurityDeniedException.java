package com.roacg.core.model.exception;

import com.roacg.core.model.enums.RoApiStatusEnum;

/**
 * 403 未经认证
 */
public class SecurityDeniedException extends RoApiException {

    public SecurityDeniedException() {
        super(RoApiStatusEnum.UNAUTHORIZED, "FORBIDDEN");
    }

    public SecurityDeniedException(String message) {
        super(RoApiStatusEnum.UNAUTHORIZED, message);
    }
}
