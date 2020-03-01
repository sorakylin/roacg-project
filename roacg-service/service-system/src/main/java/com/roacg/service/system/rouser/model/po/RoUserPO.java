package com.roacg.service.system.rouser.model.po;

import com.roacg.core.model.db.support.VirtualDeleteSupportEntity;
import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "tb_ro_user")
public class RoUserPO extends VirtualDeleteSupportEntity {

    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码，加密存储
     */
    private String password;

}