package com.roacg.service.system.rouser.service;

import com.roacg.service.system.rouser.dao.RoUserMapper;
import com.roacg.service.system.rouser.dao.RoleMapper;
import com.roacg.service.system.rouser.model.dto.MinimumRoleDTO;
import com.roacg.service.system.rouser.model.dto.MinimumUserDTO;
import com.roacg.service.system.rouser.model.po.RoUserPO;
import com.roacg.service.system.rouser.model.po.RolePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoUserService {

    @Autowired
    private RoUserMapper roUserMapper;

    @Autowired
    private RoleMapper roleMapper;

    public Optional<MinimumUserDTO> findMinimumUser(String username) {

        Optional<RoUserPO> findResult = roUserMapper.findOneByUserName(username);

        return findResult.map(res -> {
            MinimumUserDTO dto = new MinimumUserDTO();
            dto.setUserId(res.getUserId());
            dto.setUserName(res.getUserName());
            dto.setPassword(res.getPassword());
            return dto;
        });
    }

    public List<MinimumRoleDTO> findUserMinimumRole(Long userId) {

        List<RolePO> findResult = roleMapper.findRolesByUserId(userId);

        return findResult.stream().map(res -> {
            MinimumRoleDTO dto = new MinimumRoleDTO();
            dto.setRoleId(res.getRoleId());
            dto.setParentId(res.getParentId());
            dto.setName(res.getName());
            dto.setEnName(res.getEnName());
            return dto;
        }).collect(Collectors.toList());
    }

}
