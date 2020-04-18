package com.roacg.core.base.enmus;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 不同微服务之间的代号
 */
@AllArgsConstructor
@Getter
public enum ServiceCode {

    GATEWAY("service-gateway", "g"),
    SYSTEM("service-system", "s"),
    TC("service-tc", "tc");  //Translation collaboration

    private String applicationName;

    private String code;

}
