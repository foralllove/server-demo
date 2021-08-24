package com.xxx.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 描述：
 * 密码工具类BCryptPasswordEncoder是一个密码加密工具类，它可以实现不可逆的加密
 * 放到这里是因为在WebSecurityConfig中会出现循环依赖问题
 *
 * @author Jarvan
 */
@Configuration
public class DefaultPasswordConfig {
    /**
     * 装配BCryptPasswordEncoder用户密码的匹配
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
