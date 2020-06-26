package com.roacg.api.system.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserNameRDTO implements Serializable {

    private Long userId;
    
    private String userName;

    private String avatar;
}