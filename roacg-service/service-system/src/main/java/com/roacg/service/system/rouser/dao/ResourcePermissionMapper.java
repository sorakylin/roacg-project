package com.roacg.service.system.rouser.dao;
import com.roacg.service.system.rouser.model.po.ResourcePermissionPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourcePermissionMapper {

    int insertResourcePermission(@Param("permission") ResourcePermissionPO resourcePermission);

    List<ResourcePermissionPO> findAll();


}
