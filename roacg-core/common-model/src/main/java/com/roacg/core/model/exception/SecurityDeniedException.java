package com.roacg.core.model.exception;

import com.roacg.core.model.enums.RoApiStatusEnum;

/**
 * 403 未经授权
 */
public class SecurityDeniedException extends RoApiException {

    public SecurityDeniedException() {
        super(RoApiStatusEnum.FORBIDDEN, "Forbidden");
    }

    public SecurityDeniedException(String message) {
        super(RoApiStatusEnum.FORBIDDEN, message);
    }
}
