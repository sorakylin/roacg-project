package com.roacg.service.system.security.service;

import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.service.system.rouser.model.dto.MinimumRoleDTO;
import com.roacg.service.system.rouser.model.dto.MinimumUserDTO;
import com.roacg.service.system.rouser.service.RoUserService;
import com.roacg.service.system.security.model.AuthenticationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("authenticationUserService")
public class AuthenticationUserService implements UserDetailsService {

    @Autowired
    private RoUserService userService;

    /**
     * 这里返回的用户最终会和{@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
     * 这个里面的用户名密码进行比对，如果成功了就走 success handler 失败反之
     *
     * @param username 用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MinimumUserDTO user = userService.findMinimumUser(username)
                .orElseThrow(() -> new RoApiException(RoApiStatusEnum.ILLEGAL_PARAM, "User not found!"));

        List<MinimumRoleDTO> roles = userService.findUserMinimumRole(user.getUserId());


        List<SimpleGrantedAuthority> authorities = roles
                .stream()
                .map(per -> new SimpleGrantedAuthority(per.getEnName()))
                .collect(Collectors.toList());

        return AuthenticationUser.from(user).setAuthorities(authorities);
    }
}
