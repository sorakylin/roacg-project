package com.roacg.api.system.service;

import com.roacg.api.system.model.UserNameRDTO;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface UserRService {

    default UserNameRDTO findUserName(Long userId) {
        if (userId == null) return null;
        List<UserNameRDTO> result = findUserName(new Long[]{userId});
        if (result == null || result.isEmpty()) return null;
        return result.get(0);
    }

    default List<UserNameRDTO> findUserName(Long... userIds) {
        if (userIds == null || userIds.length == 0) return Collections.emptyList();
        return findUserName(Arrays.asList(userIds));
    }


    /**
     * 查询用户名字 ,只会得到 ID、名字 等基础信息
     */
    List<UserNameRDTO> findUserName(Collection<Long> userIds);
}
