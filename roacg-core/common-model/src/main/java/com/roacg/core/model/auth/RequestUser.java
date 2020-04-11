package com.roacg.core.model.auth;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
public class RequestUser implements Serializable {

    private Long id;

    private String name;

    private String credentials;

    private String credentialsType;

    private List<String> authorities;

    public Boolean hasLogin() {
        return Objects.nonNull(credentialsType) && !CredentialsType.NONE.equals(credentialsType);
    }

    public static RequestUser guest() {
        return RequestUser.builder().credentialsType(CredentialsType.NONE).authorities(List.of()).build();
    }
}
