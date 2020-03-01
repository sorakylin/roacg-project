package com.roacg.service.system.rouser.model.dto;

import lombok.Data;

import java.io.Serializable;

//最低限度的用户基本信息
@Data
public class MinimumUserDTO implements Serializable {

    private Long userId;
    private String userName;
    private String password;

}
