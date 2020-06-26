package com.roacg.service.system.rouser.dao;

import com.roacg.service.system.rouser.model.dto.SimpleUserDTO;
import com.roacg.service.system.rouser.model.po.RoUserPO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//@Mapper
public interface RoUserMapper {

    int insertUser(@Param("user") RoUserPO user);

    Optional<RoUserPO> findOneByUserName(@Param("userName") String userName);

    Optional<RoUserPO> findOneByUserId(@Param("userId") Long userId);

    /**
     * 查出简单的用户信息
     *
     * @param userIds 用户ID集合
     */
    List<SimpleUserDTO> findSimpleUserByIds(@Param("userIds") Collection<Long> userIds);

}