package com.roacg.service.system.rouser.service.remote;

import com.roacg.api.system.model.UserNameRDTO;
import com.roacg.api.system.service.UserRService;
import com.roacg.core.utils.bean.BeanMapper;
import com.roacg.service.system.rouser.dao.RoUserMapper;
import com.roacg.service.system.rouser.model.dto.SimpleUserDTO;
import com.roacg.service.system.rouser.service.RoUserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRServiceImpl implements UserRService {

    @Autowired
    private RoUserMapper roUserMapper;
    @Autowired
    private RoUserService roUserService;


    @Override
    public List<UserNameRDTO> findUserName(Collection<Long> userIds) {
        List<SimpleUserDTO> users = roUserMapper.findSimpleUserByIds(userIds);
        List<UserNameRDTO> result = users.stream()
                .map(u -> BeanMapper.map(u, UserNameRDTO.class))
                .collect(Collectors.toList());

        return result;
    }
}
