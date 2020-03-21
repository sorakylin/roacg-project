package com.roacg.service.system.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.roacg.core.model.enums.RoApiStatusEnum;
import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.Optional;

@JsonSerialize(using = ROAuth2ExceptionSerializer.class)
public class ROAuth2Exception extends OAuth2Exception {

    private String oauth2ErrorCode;

    private int httpErrorCode;

    @Getter
    private int code;

    public ROAuth2Exception(OAuth2Exception e) {
        super(e.getMessage(), e);

        this.oauth2ErrorCode = e.getOAuth2ErrorCode();
        this.httpErrorCode = e.getHttpErrorCode();


        Optional<RoApiStatusEnum> codeEnum = RoApiStatusEnum.forCode(this.httpErrorCode);
        if (codeEnum.isPresent()) {
            code = codeEnum.map(en -> en.getCode()).get();
        } else {
            code = RoApiStatusEnum.OTHER.getCode();
        }
    }

    @Override
    public String getOAuth2ErrorCode() {
        return oauth2ErrorCode;
    }


    @Override
    public int getHttpErrorCode() {
        return httpErrorCode;
    }


}
