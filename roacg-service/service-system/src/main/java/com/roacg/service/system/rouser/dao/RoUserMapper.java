package com.roacg.service.system.rouser.dao;

import com.roacg.service.system.rouser.model.po.RoUserPO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

//@Mapper
public interface RoUserMapper {

    int insertUser(@Param("user") RoUserPO user);

    Optional<RoUserPO> findOneByUserName(@Param("userName") String userName);

    Optional<RoUserPO> findOneByUserId(@Param("userId") Long userId);

}