package com.roacg.service.gateway.route.data;

import lombok.Data;

/**
 * 一个登陆的请求
 */
@Data
public class LoginRequest {

    private String username;

    private String password;
}
