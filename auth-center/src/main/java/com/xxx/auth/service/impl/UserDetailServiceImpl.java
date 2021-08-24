package com.xxx.auth.service.impl;

import com.xxx.common.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

/**
 * 描述：UserDetailServiceImpl
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 16:00
 * @company 数海掌讯
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService, SocialUserDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = new SysUser();
        sysUser.setId(10000L);
        sysUser.setPassword(passwordEncoder.encode("Aa1234567!"));
        return sysUser;
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        return new SysUser();
    }
}
