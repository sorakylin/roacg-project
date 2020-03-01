package com.roacg.service.system.rouser.dao;

import com.roacg.service.system.rouser.model.po.RolePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {

    int insertRole(@Param("role") RolePO role);

    List<RolePO> findRolesByUserId(@Param("userId") Long userId);

    List<RolePO> findAllByParentId(@Param("parentId")Long parentId);

    List<RolePO> findAll();

}
